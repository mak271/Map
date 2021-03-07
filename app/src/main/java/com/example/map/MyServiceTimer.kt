package com.example.map

import android.app.Service
import android.content.Intent
import android.os.*
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class MyServiceTimer : Service() {

    var serviceLooper: Looper? = null
    private var handlerService: HandlerService? = null
    var count: Double = 0.0

    private var myTimerTask: TimerTask? = null
    private lateinit var timer: Timer

    companion object {
        val ACTION_COUNT_BROADCAST: String = MyServiceTimer::class.java.name + "CountBroadcast"
        val ACTION_TIME_BROADCAST: String = MyServiceTimer::class.java.name + "TimeBroadcast"
        val EXTRA_TIME = "extra_time"
        val EXTRA_START = "extra_start"
        val EXTRA_END = "extra_end"
    }

    private inner class HandlerService(looper: Looper): Handler(looper) {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {

            if (myTimerTask == null)
                startTimer()

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

                    val calendar: Calendar = Calendar.getInstance()
                    val sdf = SimpleDateFormat("hh:mm:ss")
                    count++


                    if ((MapsActivity.distance1[0] <= MapsActivity.radius) || (MapsActivity.distance2[0] <= MapsActivity.radius))
                    {

                        sendBroadcastMessage(getTimerText())
                        val startTime = sdf.format(calendar.time)

                    }
                    else {
                        resetTimer()
                        val endTime = sdf.format(calendar.time)
                        //sendBroadcastTime(startTime, endTime)
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


    private fun sendBroadcastMessage(time: String) {
        val intent = Intent(ACTION_COUNT_BROADCAST)
        intent.putExtra(EXTRA_TIME, time)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendBroadcastTime(start: String, end: String) {
        val intent = Intent(ACTION_TIME_BROADCAST)
        intent.putExtra(EXTRA_START, start)
        intent.putExtra(EXTRA_END, end)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

//    private fun sendLocation(location: Location) {
//        val intent = Intent(ACTION_LOCATION_BROADCAST)
//        intent.putExtra(EXTRA_LATITUDE, location.latitude)
//        intent.putExtra(EXTRA_LONGTITUDE, location.longitude)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//    }
}
