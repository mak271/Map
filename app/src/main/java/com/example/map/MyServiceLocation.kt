package com.example.map

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.widget.Toast
import com.google.android.gms.location.LocationResult

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

                    }
                }
            }
        }
    }




}