package com.example.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.LocationResult
import com.example.map.DB.DatabaseORMModel
import com.example.map.DatabaseActivity.Companion.myViewModel

class MyServiceLocation : BroadcastReceiver() {

    companion object {
        val ACTION_PROCESS_UPDATE = "com.example.map.UPDATE_LOCATION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (action.equals(ACTION_PROCESS_UPDATE)) {
                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    val location: Location = result.lastLocation
                    try {

                        MapsActivity.getMainInstance().update(location)

                    } catch (e: Exception) {
                        Toast.makeText(context, location.toString(), Toast.LENGTH_SHORT).show()

                        Location.distanceBetween(
                                location.latitude,
                                location.longitude,
                                MapsActivity.ivanovo1.latitude,
                                MapsActivity.ivanovo1.longitude,
                                MapsActivity.distance1
                        )

                        Location.distanceBetween(
                                location.latitude,
                                location.longitude,
                                MapsActivity.ivanovo2.latitude,
                                MapsActivity.ivanovo2.longitude,
                                MapsActivity.distance2
                        )

                    }
                }
            }
        }
    }




}