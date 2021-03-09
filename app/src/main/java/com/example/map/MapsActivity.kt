package com.example.map

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color.rgb
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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

    private lateinit var mMap: GoogleMap
    private lateinit var circleOptions1: CircleOptions
    private lateinit var circleOptions2: CircleOptions

    private lateinit var tv_timer: TextView
    private lateinit var text_view: TextView
    private lateinit var btn_edit: Button

    var markerCount: Int = 0

    private var mtemp: Marker? = null
    private var circle1: Circle? = null
    private var circle2: Circle? = null
    private var ctemp: Circle? = null

    companion object {
        const val radius = 30.0

        var rad: Double? = null

        val ivanovo1 = LatLng(56.99545039, 40.96074659)
        val ivanovo2 = LatLng(56.99670852, 40.96133581)
        var m1: Marker? = null
        var m2: Marker? = null
        val distance1 = FloatArray(2)
        val distance2 = FloatArray(2)
        val distance3 = FloatArray(2)



        var instance: MapsActivity? = null

        fun getMainInstance(): MapsActivity {
            return instance!!
        }
    }

    fun update(value: Location) {
        this@MapsActivity.runOnUiThread {
            text_view.text = value.toString()
        }
    }

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        instance = this

        text_view = findViewById(R.id.txt_location)
        btn_edit = findViewById(R.id.btn_edit)

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

        tv_timer = findViewById(R.id.tv_timer)

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
        circleOptions1 = CircleOptions()
        circleOptions2 = CircleOptions()

        btn_edit.setOnClickListener {
            val li = LayoutInflater.from(this)
            val promptsView: View = li.inflate(R.layout.prompt, null)

            val myDialogBuilder = AlertDialog.Builder(this)
            myDialogBuilder.setView(promptsView)

            val userInput: EditText = promptsView.findViewById(R.id.input_text)

            myDialogBuilder.setCancelable(false)
                .setPositiveButton("Ok") { dialog, id -> rad = userInput.text.toString().toDouble() }
                .setNegativeButton("Отмена") { dialog, id -> dialog.cancel() }

            val alertDialog: AlertDialog = myDialogBuilder.create()
            alertDialog.show()
        }

        mMap.setOnMapClickListener {

            if (rad != null) {

                val circleOptions = CircleOptions()
                        .center(it)
                        .radius(rad!!)

                ctemp = mMap.addCircle(circleOptions)
                mtemp = mMap.addMarker(MarkerOptions().position(it).title("Marker"))
                markerCount++

                if (markerCount > 2) {
                    val myDialogFragment = MyDialogFragment()
                    val manager = supportFragmentManager
                    myDialogFragment.show(manager, "myDialog")
                }

                if (m1 == null) {
                    m1 = mtemp
                    circle1 = ctemp
                } else if (m2 == null) {
                    m2 = mtemp
                    circle2 = ctemp
                }

            } else Toast.makeText(this, "Введите радиус", Toast.LENGTH_SHORT).show()

        }

        val c1 = circleOptions1.center(ivanovo1).radius(radius)
        val c2 = circleOptions2.center(ivanovo2).radius(radius)
        mMap.addCircle(c1)
        mMap.addCircle(c2)
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
        m1 = mtemp
        circle1?.remove()
        circle1 = ctemp
    }

    fun secondClicked() {
        Toast.makeText(this, "Вы удалили вторую точку", Toast.LENGTH_SHORT).show()
        m2?.remove()
        m2 = mtemp
        circle2?.remove()
        circle2 = ctemp
    }

    fun thirdClicked() {
        Toast.makeText(this, "Отмена действия", Toast.LENGTH_SHORT).show()
        mtemp?.remove()
        ctemp?.remove()
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





}