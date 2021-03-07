package com.example.map

import android.Manifest
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
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
    private var currentLocation: Location? = null

    companion object {
        val radius = 30.0
        val ivanovo1 = LatLng(56.99545039, 40.96074659)
        val ivanovo2 = LatLng(56.99670852, 40.96133581)
        val distance1 = FloatArray(2)
        val distance2 = FloatArray(2)

        var instance: MapsActivity? = null

        fun getMainInstance(): MapsActivity {
            return instance!!
        }
    }

    fun update(value: Location) {
        this@MapsActivity.runOnUiThread {
            text_view.text = value.toString()
            currentLocation = value
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

                        if (currentLocation != null) {
                            Location.distanceBetween(
                                currentLocation!!.latitude,
                                currentLocation!!.longitude,
                                ivanovo1.latitude,
                                ivanovo1.longitude,
                                distance1
                            )

                            Location.distanceBetween(
                                currentLocation!!.latitude,
                                currentLocation!!.longitude,
                                ivanovo2.latitude,
                                ivanovo2.longitude,
                                distance2
                            )

                            println("${distance1[0]}/${distance2[0]}")
                        }


                    }

                }, IntentFilter(MyServiceTimer.ACTION_COUNT_BROADCAST)
        )

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        circleOptions1 = CircleOptions()
        circleOptions2 = CircleOptions()
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