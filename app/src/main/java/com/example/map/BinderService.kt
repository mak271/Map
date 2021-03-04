package com.example.map

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*

class BinderService: Service() {
    private val binder: IBinder = LocalBinder()
    private var time: Double = 0.0

    inner class LocalBinder: Binder() {
        val service: BinderService
            get() = this@BinderService
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    val timer: Double
        get() = (
                try {
                    Thread.sleep(1000)
                    time++
                } catch (e: Exception) {
                    e.message.toString()
                    time--
                }
                )

    var timer1: Double = 0.0
        get() = (time++)






}