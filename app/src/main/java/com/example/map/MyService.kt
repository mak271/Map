package com.example.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class MyService : Service() {

    var serviceLooper: Looper? = null
    private var handlerService: HandlerService? = null
    var count: Double = 0.0

    private var myTimerTask: TimerTask? = null
    private lateinit var timer: Timer

    private val distance1 = FloatArray(2)
    private val distance2 = FloatArray(2)

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var currentLocation: Location? = null
    private var PERMISSION_ID = 1000

    val radius = MapsActivity.radius
    val ivanovo1 = MapsActivity.ivanovo1
    val ivanovo2 = MapsActivity.ivanovo2
    private lateinit var circleCenter: LatLng

    companion object {
        val ACTION_COUNT_BROADCAST: String = MyService::class.java.name + "CountBroadcast"
        val EXTRA_COUNT = "extra_count"
    }

    private inner class HandlerService(looper: Looper): Handler(looper) {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {
            startTimer()
            getLastLocation()

        }

    }

    private fun resetTimer() {
        count = 0.0
        sendBroadcastMessage(formatTime(0,0,0))
    }

    private fun startTimer() {
        myTimerTask = object : TimerTask() {
            override fun run() {
                thread {

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

                    count++

                    try {

                        println("${distance1[0]}/${distance2[0]}")
                        if ((distance1[0] <= radius) || (distance2[0] <= radius))
                            sendBroadcastMessage(getTimerText())
                        else
                            resetTimer()


                    }catch (e: Exception) {
                        println("${distance1[0]}/${distance2[0]}")
                        if ((distance1[0] <= radius) || (distance2[0] <= radius))
                            sendBroadcastMessage(getTimerText())
                        else
                            resetTimer()
                    }


                }
            }
        }
        timer.schedule(myTimerTask, 1000, 1000)
    }

    private fun formatTime(seconds: Int, minutes: Int, hours: Int): String {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds)
    }

    private fun getTimerText(): String {

        val rounded = count.roundToInt()

        val seconds = rounded % 86400 % 3600 % 60
        val minutes = rounded % 86400 % 3600 / 60
        val hours = rounded % 86400 / 3600

        return formatTime(seconds, minutes, hours)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        val thread = HandlerThread("ServiceStartArgument", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        timer = Timer()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        serviceLooper = thread.looper
        handlerService = HandlerService(serviceLooper!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val message: Message = handlerService!!.obtainMessage()
        message.arg1 = startId
        handlerService?.handleMessage(message)


        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }



    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLastLocation() {
        if(CheckPermission()) {
            if(isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    getNewLocation()

                }
            } else Toast.makeText(this, "Please enable your location service", Toast.LENGTH_LONG).show()

        } else RequestPermission()
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val lastLocation = p0?.lastLocation
            currentLocation = lastLocation

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
                MapsActivity(),
                arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ), PERMISSION_ID
        )
        getLastLocation()
    }

    private fun sendBroadcastMessage(time: String) {
        val intent = Intent(ACTION_COUNT_BROADCAST)
        intent.putExtra(EXTRA_COUNT, time)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

//    private fun sendLocation(location: Location) {
//        val intent = Intent(ACTION_LOCATION_BROADCAST)
//        intent.putExtra(EXTRA_LATITUDE, location.latitude)
//        intent.putExtra(EXTRA_LONGTITUDE, location.longitude)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//    }
}
