package com.example.map

import android.app.Service
import android.content.Intent
import android.os.*

class MyService : Service() {

    var serviceLooper: Looper? = null
    private var handlerService: HandlerService? = null

    private inner class HandlerService(looper: Looper): Handler(looper) {
        override fun handleMessage(msg: Message) {
            val endTime = System.currentTimeMillis() + 5000

            while (System.currentTimeMillis() < endTime)
                println(System.currentTimeMillis())

            stopSelf(msg.arg1)
        }

    }

    override fun onCreate() {
        val thread = HandlerThread("ServiceStartArgument", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()

        serviceLooper = thread.looper
        handlerService = HandlerService(serviceLooper!!)
    }

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
}