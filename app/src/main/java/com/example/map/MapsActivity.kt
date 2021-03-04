package com.example.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Flow
import kotlin.math.roundToInt


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var circleOptions: CircleOptions
    private val distance = FloatArray(2)

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var currentLocation: Location? = null

    val radius = 30.0
    val ivanovo = LatLng(56.99545039, 40.96074659)

    private lateinit var tv_timer: TextView
    private var myTimerTask: TimerTask? = null
    private lateinit var timer: Timer
    var time: Double = 0.0

    var rounded: Int = 0
    var seconds: Int = 0
    var minutes : Int = 0
    var hours : Int = 0
    private var PERMISSION_ID = 1000


    var service: BinderService? = null
    var bound = false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        tv_timer = findViewById(R.id.tv_timer)
        timer = Timer()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

//        findViewById<Button>(R.id.btn_number).setOnClickListener {
//            val number = service?.randomNumber
//            Toast.makeText(this, "number: $number", Toast.LENGTH_SHORT).show()
//        }

    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, BinderService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }


    private val connection: ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BinderService.LocalBinder
            this@MapsActivity.service = binder.service
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        circleOptions = CircleOptions()
        circleOptions.center(ivanovo).radius(radius)
        mMap.addCircle(circleOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ivanovo, 18F))

    }

    private fun enableMyLocation() {
        if (!::mMap.isInitialized) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
    }

    private fun resetTimer() {
        myTimerTask?.cancel()
        service!!.timer1 = 0.0
        time = 0.0
        tv_timer.text = formatTime(0,0,0)
    }

    private fun startTimer() {
        myTimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    tv_timer.text = getTimerText()
                }
            }
        }
        timer.schedule(myTimerTask, 1000, 1000)
    }

    private fun formatTime(seconds: Int, minutes: Int, hours: Int): String {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds)
    }

    private fun getTimerText(): String {

        rounded = service!!.timer1.roundToInt()

//        GlobalScope.launch(Dispatchers.Main) {
//            service!!.doSomething().collect { value ->
//                rounded = value.roundToInt()
//
//            }
//        }
        seconds = rounded % 86400 % 3600 % 60
        minutes = rounded % 86400 % 3600 / 60
        hours = rounded % 86400 / 3600
        println(seconds)

        if (rounded != 0) {
            return formatTime(seconds, minutes, hours)
        }
        else
            return formatTime(0,0,0)


    }




    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLastLocation() {
        if(CheckPermission()) {
            if(isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result

                        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)

                        getNewLocation()

                }
            } else Toast.makeText(this, "Please enable your location service", Toast.LENGTH_LONG).show()

        } else RequestPermission()
    }


    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 1000
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val lastLocation = p0?.lastLocation
            currentLocation = lastLocation
            enableMyLocation()
            time++

            Location.distanceBetween(
                    currentLocation!!.latitude,
                    currentLocation!!.longitude,
                    circleOptions.center.latitude,
                    circleOptions.center.longitude,
                    distance
            )

            if (distance[0] <= radius) {
                startTimer()
            } else
                resetTimer()



        }
    }

    private fun isLocationEnabled():Boolean {
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    private fun CheckPermission():Boolean  {
        if(
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun RequestPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ), PERMISSION_ID
        )
        getLastLocation()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have the permission")
            }
        }
    }



}