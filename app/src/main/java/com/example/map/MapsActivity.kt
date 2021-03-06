package com.example.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
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
    private lateinit var circleOptions1: CircleOptions
    private lateinit var circleOptions2: CircleOptions

    private lateinit var tv_timer: TextView

    companion object {
        val radius = 30.0
        val ivanovo1 = LatLng(56.99545039, 40.96074659)
        val ivanovo2 = LatLng(56.99670852, 40.96133581)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        tv_timer = findViewById(R.id.tv_timer)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        startService(Intent(this, MyService::class.java))


        LocalBroadcastManager.getInstance(this).registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        val result = intent?.getStringExtra(MyService.EXTRA_COUNT)
                        tv_timer.text = result
                    }

                }, IntentFilter(MyService.ACTION_COUNT_BROADCAST)
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







}