package com.example.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
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
import java.util.*
import kotlin.math.pow


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var pref: SharedPreferences

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest


    private var PERMISSION_ID = 1000

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val ivanovo = LatLng(56.99545039, 40.96074659)
        val radius = 20.0
        val distance = FloatArray(2)
        val circleOptions = CircleOptions()
        circleOptions.center(ivanovo).radius(radius)
        mMap.addCircle(circleOptions)

        enableMyLocation()
        mMap.setOnMyLocationChangeListener {
            Location.distanceBetween(
                it.latitude,
                it.longitude,
                circleOptions.center.latitude,
                circleOptions.center.longitude,
                distance
            )

            println(distance[0])
            if (distance[0] <= radius) {
                Toast.makeText(this, "Всё круто", Toast.LENGTH_SHORT).show()
            }

        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ivanovo, 18F))


    }

    private fun enableMyLocation() {
        if (!::mMap.isInitialized) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLastLocation() {
        if(CheckPermission()) {
            if(isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if(location == null) {
                        getNewLocation()
                    } else {


                    }
                }
            } else Toast.makeText(this, "Please enable your location service", Toast.LENGTH_LONG).show()

        } else RequestPermission()
    }


    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val lastLocation = p0?.lastLocation
            if (lastLocation != null) {
                Toast.makeText(
                    this@MapsActivity,
                    "Your current coordinates are :\nLat: ${lastLocation.latitude}  ; Long: ${lastLocation.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
                enableMyLocation()
            }
        }
    }


}