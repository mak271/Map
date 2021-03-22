package com.example.map

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.map.DB.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    var myViewModel: MyViewModel? = null

    lateinit var mMap: GoogleMap
    lateinit var searchView: SearchView
    private lateinit var circleOptions3: CircleOptions
    private lateinit var circleOptions4: CircleOptions

    private lateinit var tv_timer: TextView
    private lateinit var tv_radius1: TextView
    private lateinit var tv_radius2: TextView

    private var markerCount: Int = 0
    private var rad3: Double = 30.0

    private var circle1: Circle? = null
    private var circle2: Circle? = null
    private var circle3: Circle? = null

    private var latlng: LatLng? = null

    var rad1: Double? = null
    var rad2: Double? = null

    var latlng1: LatLng? = null
    var latlng2: LatLng? = null

    val ivanovo1 = LatLng(56.99545039, 40.96074659)

    var m1: Marker? = null
    var m2: Marker? = null
    var m3: Marker? = null


    private var name:String="Default"

    var currentLocation: Location? = null

    var seekBar1: SeekBar? = null
    var seekBar2: SeekBar? = null

    companion object {

        var name1:String="First"
        var name2:String="Second"

        var instance: MapsActivity? = null

        fun getMapInstance(): MapsActivity {
            return instance!!
        }
    }

    fun update(value: Location) {
        this.runOnUiThread {
            currentLocation = value

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        tv_timer = findViewById(R.id.tv_timer)

        tv_radius1 = findViewById(R.id.tv_radius1)
        tv_radius2 = findViewById(R.id.tv_radius2)

        searchView = findViewById(R.id.sv_location)


        myViewModel = ViewModelProvider(this, ViewModelFactory()).get(MyViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location: String = searchView.query.toString()
                var addressList: List<Address>? = null

                if (location != null) {
                    val geocoder = Geocoder(this@MapsActivity)
                    try {

                        addressList = geocoder.getFromLocationName(location, 1)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val address: Address = addressList!![0]
                    val addressLatLng = LatLng(address.latitude, address.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressLatLng, 15F))

                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

        findViewById<Button>(R.id.btn_other).setOnClickListener {
            val intent = Intent(this, DatabaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            finish()
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            finish()
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_delete1).setOnClickListener {
            myViewModel?.deleteCircle1(this)
            m1?.remove()
            m1 = null
            circle1?.remove()
            circle1 = null
            rad1 = 0.0
            markerCount--
            tv_radius1.text = "0.0"
            seekBar1!!.progress = 0
            seekBar1!!.visibility = View.INVISIBLE
            tv_radius1.visibility = View.INVISIBLE
        }

        findViewById<Button>(R.id.btn_delete2).setOnClickListener {
            myViewModel?.deleteCircle2(this)
            m2?.remove()
            m2 = null
            circle2?.remove()
            circle2 = null
            rad2 = 0.0
            markerCount--
            tv_radius2.text = "0.0"
            seekBar2!!.progress = 0
            seekBar2!!.visibility = View.INVISIBLE
            tv_radius2.visibility = View.INVISIBLE
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        val result = intent?.getStringExtra(MyServiceTimer.EXTRA_TIME)
                        tv_timer.text = result
                    }
                }, IntentFilter(MyServiceTimer.ACTION_COUNT_BROADCAST)
        )

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        circleOptions3 = CircleOptions()
        circleOptions4 = CircleOptions()
        seekBar1 = findViewById(R.id.seek_bar1)
        seekBar2 = findViewById(R.id.seek_bar2)

        seekBar1!!.visibility = View.INVISIBLE
        seekBar2!!.visibility = View.INVISIBLE
        tv_radius1.visibility = View.INVISIBLE
        tv_radius2.visibility = View.INVISIBLE


        var a = 0
        myViewModel?.selectCircle1(this)?.observe(this, {
            if (a == 0) {
                a++
                if (it != null) {
                    markerCount++
                    latlng1 = LatLng(it.latitude, it.longitude)
                    rad1 = it.radius
                    tv_radius1.text = rad1.toString()
                    name1 = it.name
                    m1 = mMap.addMarker(MarkerOptions().position(latlng1!!).title(name1))
                    circleOptions3.center(latlng1).radius(it.radius)
                    circle1 = mMap.addCircle(circleOptions3)
                    seekBar1!!.progress = rad1!!.roundToInt()
                    seekBar1!!.visibility = View.VISIBLE
                    tv_radius1.visibility = View.VISIBLE
                }
            }


        })

        var b = 0
        myViewModel?.selectCircle2(this)?.observe(this, {
            if (b == 0) {
                b++
                if (it != null) {
                    markerCount++
                    latlng2 = LatLng(it.latitude, it.longitude)
                    rad2 = it.radius
                    tv_radius2.text = rad2.toString()
                    name2 = it.name
                    m2 = mMap.addMarker(MarkerOptions().position(latlng2!!).title(name2))
                    circleOptions4.center(latlng2).radius(it.radius)
                    circle2 = mMap.addCircle(circleOptions4)
                    seekBar2!!.progress = rad2!!.roundToInt()
                    seekBar2!!.visibility = View.VISIBLE
                    tv_radius2.visibility = View.VISIBLE
                }
            }


        })


        seekBar1!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                tv_radius1.text = progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

                if (latlng1 != null) {
                    rad1 = p0?.progress?.toDouble()
                    m1?.remove()
                    circle1?.remove()
                    //name1 = name
                    GlobalScope.launch(Dispatchers.IO) {
                        myViewModel?.insertCircle(this@MapsActivity, CircleModel(0, name1, rad1!!, latlng1!!.latitude, latlng1!!.longitude))
                    }

                    tv_radius1.text = rad1.toString()
                    m1 = mMap.addMarker(MarkerOptions().position(latlng1!!).title(name1))
                    circleOptions3.center(latlng1).radius(rad1!!)
                    circle1 = mMap.addCircle(circleOptions3)
                    seekBar1!!.progress = rad1!!.roundToInt()

                    if (currentLocation != null) {
                        Location.distanceBetween(
                                currentLocation!!.latitude,
                                currentLocation!!.longitude,
                                MyServiceTimer.lat1,
                                MyServiceTimer.lon1,
                                MyServiceLocation.distance1
                        )
                    }

                }


            }

        })

        seekBar2!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                tv_radius2.text = progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

                if (latlng2 != null) {
                    rad2 = p0?.progress?.toDouble()
                    m2?.remove()
                    circle2?.remove()
                    //name2 = name
                    GlobalScope.launch(Dispatchers.IO) {
                        myViewModel?.insertCircle(this@MapsActivity, CircleModel(1, name2, rad2!!, latlng2!!.latitude, latlng2!!.longitude))
                    }

                    tv_radius2.text = rad2.toString()
                    m2 = mMap.addMarker(MarkerOptions().position(latlng2!!).title(name2))
                    circleOptions4.center(latlng2).radius(rad2!!)
                    circle2 = mMap.addCircle(circleOptions4)
                    seekBar2!!.progress = rad2!!.roundToInt()

                    if (currentLocation != null) {
                        Location.distanceBetween(
                                currentLocation!!.latitude,
                                currentLocation!!.longitude,
                                MyServiceTimer.lat2,
                                MyServiceTimer.lon2,
                                MyServiceLocation.distance2
                        )
                    }
                }
            }

        })

        mMap.setOnMapClickListener {



            val li = LayoutInflater.from(this)
            val promptsView: View = li.inflate(R.layout.prompt, null)
            val mDialogBuilder = AlertDialog.Builder(this)
            mDialogBuilder.setView(promptsView)
            val nameInput = promptsView.findViewById<View>(R.id.input_text) as EditText

            mDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", DialogInterface.OnClickListener
                    { dialog, id ->

                        if (nameInput.toString().isNotEmpty()) {
                            name = "#${nameInput.text}"
                            if (name == "#")
                                name = "Default"
                            else
                                name = nameInput.text.toString()
                        }

                        draw(it, name)

                    })
                    .setNegativeButton("Отмена") { dialog, id -> dialog.cancel() }
            val alertDialog = mDialogBuilder.create()
            alertDialog.show()




        }

        enableMyLocation()

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ivanovo1, 17F))

    }

    private fun draw(location: LatLng, myName: String) {
        latlng = location

        m3 = mMap.addMarker(MarkerOptions().position(latlng!!).title(myName))
        circleOptions3.center(latlng).radius(rad3)
        circle3 = mMap.addCircle(circleOptions3)

        markerCount++

        if (markerCount > 2) {
            val myDialogFragment = MyDialogFragment()
            val manager = supportFragmentManager
            myDialogFragment.show(manager, "myDialog")
        }

        if (m1 == null) {
            seekBar1!!.visibility = View.VISIBLE
            tv_radius1.visibility = View.VISIBLE
            m1 = m3
            circle1 = circle3
            latlng1 = latlng
            rad1 = rad3
            name1 = myName
            myViewModel?.insertCircle(this, CircleModel(0, name1, rad1!!, latlng1!!.latitude, latlng1!!.longitude))

            if (currentLocation != null) {
                Location.distanceBetween(
                        currentLocation!!.latitude,
                        currentLocation!!.longitude,
                        MyServiceTimer.lat1,
                        MyServiceTimer.lon1,
                        MyServiceLocation.distance1
                )
            }
        } else if (m2 == null) {
            seekBar2!!.visibility = View.VISIBLE
            tv_radius2.visibility = View.VISIBLE
            m2 = m3
            circle2 = circle3
            latlng2 = latlng
            rad2 = rad3
            name2 = myName
            myViewModel?.insertCircle(this, CircleModel(1, name2, rad2!!, latlng2!!.latitude, latlng2!!.longitude))

            if (currentLocation != null) {
                Location.distanceBetween(
                        currentLocation!!.latitude,
                        currentLocation!!.longitude,
                        MyServiceTimer.lat2,
                        MyServiceTimer.lon2,
                        MyServiceLocation.distance2
                )
            }

        }
    }

    private fun enableMyLocation() {
        if (!::mMap.isInitialized) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
    }

    fun firstClicked() {
        Toast.makeText(this, "Вы удалили первую точку", Toast.LENGTH_SHORT).show()
        m1?.remove()
        circle1?.remove()
        tv_radius1.text = "0.0"
        seekBar1?.progress = 0
        m1 = m3
        circle1 = circle3
        latlng1 = latlng
        markerCount--
        name1 = name
        myViewModel?.insertCircle(this, CircleModel(0, name1, rad1!!, latlng1!!.latitude, latlng1!!.longitude))
    }

    fun secondClicked() {
        Toast.makeText(this, "Вы удалили вторую точку", Toast.LENGTH_SHORT).show()
        m2?.remove()
        circle2?.remove()
        tv_radius2.text = "0.0"
        seekBar2?.progress = 0
        m2 = m3
        circle2 = circle3
        latlng2 = latlng
        markerCount--
        name2 = name
        myViewModel?.insertCircle(this, CircleModel(1, name2, rad2!!, latlng2!!.latitude, latlng2!!.longitude))
    }

    fun thirdClicked() {
        Toast.makeText(this, "Отмена действия", Toast.LENGTH_SHORT).show()
        m3?.remove()
        circle3?.remove()
        markerCount--
    }




}