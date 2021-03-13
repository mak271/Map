package com.example.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.LocationResult

class MyServiceLocation : BroadcastReceiver() {

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

                        Location.distanceBetween(
                                location.latitude,
                                location.longitude,
                                MyServiceTimer.lat1,
                                MyServiceTimer.lon1,
                                distance1
                        )

                        Location.distanceBetween(
                                location.latitude,
                                location.longitude,
                                MyServiceTimer.lat2,
                                MyServiceTimer.lon2,
                                distance2
                        )

                        println("${distance1[0]}/${distance2[0]}")

                    } catch (e: Exception) {
                        Toast.makeText(context, location.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }




}