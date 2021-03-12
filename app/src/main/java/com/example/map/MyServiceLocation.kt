package com.example.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.LocationResult
import com.example.map.DB.DatabaseORMModel
import com.example.map.DatabaseActivity.Companion.myViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MyServiceLocation : BroadcastReceiver() {

    private var lat: Double = 0.0
    private var lon: Double = 0.0



    companion object {
        val ACTION_PROCESS_UPDATE = "com.example.map.UPDATE_LOCATION"

        val distance1 = FloatArray(2)
        val distance2 = FloatArray(2)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (action.equals(ACTION_PROCESS_UPDATE)) {
                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    val location: Location = result.lastLocation
                    try {

                        if (MapsActivity.latlng1 != null) {

                            lat = MapsActivity.latlng1?.latitude!!
                            lon = MapsActivity.latlng1?.longitude!!

                            Location.distanceBetween(
                                    location.latitude,
                                    location.longitude,
                                    lat,
                                    lon,
                                    distance1
                            )

                        }

                        if (MapsActivity.latlng2 != null) {
                            Location.distanceBetween(
                                    location.latitude,
                                    location.longitude,
                                    MapsActivity.latlng2?.latitude!!,
                                    MapsActivity.latlng2?.longitude!!,
                                    distance2
                            )

                        }

                        println("${distance1[0]}/${distance2[0]}")

                        MapsActivity.getMainInstance().update(location)

                    } catch (e: Exception) {
                        Toast.makeText(context, location.toString(), Toast.LENGTH_SHORT).show()

                        Location.distanceBetween(
                                location.latitude,
                                location.longitude,
                                lat,
                                lon,
                                distance1
                        )

                        println("${distance1[0]}/${distance2[0]}")

                    }
                }
            }
        }
    }




}