package com.example.map

import android.Manifest
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.map.DB.MyViewModel
import com.example.map.DB.ViewModelFactory
import com.example.map.DB.CircleModel1
import com.example.map.DB.CircleModel2
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    var myViewModel: MyViewModel? = null

    lateinit var mMap: GoogleMap
    private lateinit var circleOptions3: CircleOptions
    private lateinit var circleOptions4: CircleOptions

    private lateinit var tv_timer: TextView
    private lateinit var tv_radius1: TextView
    private lateinit var tv_radius2: TextView

    var markerCount: Int = 0
    var rad3: Double = 20.0

    private var circle1: Circle? = null
    private var circle2: Circle? = null
    private var circle3: Circle? = null

    private var latlng: LatLng? = null

    var pref: SharedPreferences? = null

    var rad1: Double? = null
    var rad2: Double? = null

    var latlng1: LatLng? = null
    var latlng2: LatLng? = null

    val ivanovo1 = LatLng(56.99545039, 40.96074659)

    var m1: Marker? = null
    var m2: Marker? = null
    var m3: Marker? = null

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        tv_timer = findViewById(R.id.tv_timer)

        tv_radius1 = findViewById(R.id.tv_radius1)
        tv_radius2 = findViewById(R.id.tv_radius2)

        myViewModel = ViewModelProvider(this, ViewModelFactory()).get(MyViewModel::class.java)

        pref = getSharedPreferences("Table", Context.MODE_PRIVATE)
        markerCount = pref?.getInt("Count", markerCount)!!



        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        updateLocation()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(this@MapsActivity, "You need accept", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {

                    }

                }).check()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.startService(Intent(this, MyServiceTimer::class.java))

        findViewById<Button>(R.id.btn_other).setOnClickListener {
            startActivity(Intent(this, DatabaseActivity::class.java))
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


        myViewModel?.selectRadius1(this)?.observe(this, {
            if (it != null) {
                latlng1 = LatLng(it.latitude, it.longitude)
                rad1 = it.radius
                m1 = mMap.addMarker(MarkerOptions().position(latlng1!!).title("Marker").snippet("First"))
                circleOptions3.center(latlng1).radius(it.radius)
                circle1 = mMap.addCircle(circleOptions3)
            }


        })

        myViewModel?.selectRadius2(this)?.observe(this, {
            if (it != null) {
                latlng2 = LatLng(it.latitude, it.longitude)
                rad2 = it.radius
                m2 = mMap.addMarker(MarkerOptions().position(latlng2!!).title("Marker").snippet("Second"))
                circleOptions4.center(latlng2).radius(it.radius)
                circle2 = mMap.addCircle(circleOptions4)
            }

        })

        val seekBar1: SeekBar = findViewById(R.id.seek_bar1)
        seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
                    myViewModel?.insertCircle1(this@MapsActivity, CircleModel1(0, rad1!!, latlng1?.latitude!!, latlng1?.longitude!!))
                }


            }

        })

        val seekBar2: SeekBar = findViewById(R.id.seek_bar2)
        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
                    myViewModel?.insertCircle2(this@MapsActivity, CircleModel2(0, rad2!!, latlng2?.latitude!!, latlng2?.longitude!!))
                }
            }

        })

        mMap.setOnMapClickListener {

                latlng = it

                m3 = mMap.addMarker(MarkerOptions().position(latlng!!).title("Marker"))
                circleOptions3.center(latlng).radius(rad3)
                circle3 = mMap.addCircle(circleOptions3)

                markerCount++
                saveCount(markerCount)

                if (markerCount > 2) {
                    val myDialogFragment = MyDialogFragment()
                    val manager = supportFragmentManager
                    myDialogFragment.show(manager, "myDialog")
                }

                if (m1 == null) {
                    m1 = m3
                    circle1 = circle3
                    latlng1 = latlng
                    rad1 = rad3
                } else if (m2 == null) {
                    m2 = m3
                    circle2 = circle3
                    latlng2 = latlng
                    rad2 = rad3
                }

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ivanovo1, 17F))
        enableMyLocation()

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
        m1 = m3
        circle1 = circle3
        latlng1 = latlng
    }

    fun secondClicked() {
        Toast.makeText(this, "Вы удалили вторую точку", Toast.LENGTH_SHORT).show()
        m2?.remove()
        circle2?.remove()
        m2 = m3
        circle2 = circle3
        latlng2 = latlng
    }

    fun thirdClicked() {
        Toast.makeText(this, "Отмена действия", Toast.LENGTH_SHORT).show()
        m3?.remove()
        circle3?.remove()
    }


    private fun updateLocation() {
        buildLocationRequest()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this, MyServiceLocation::class.java)
        intent.action = MyServiceLocation.ACTION_PROCESS_UPDATE
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 500
        locationRequest.smallestDisplacement = 10f
    }

    private fun saveCount(res: Int) {
        val editor = pref?.edit()
        editor?.putInt("Count", res)
        editor?.apply()
    }



}