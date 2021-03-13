package com.example.map

import android.content.Intent
import android.os.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.map.DB.DatabaseORMModel
import com.example.map.DB.MyViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class MyServiceTimer : LifecycleService() {

    var serviceLooper: Looper? = null
    private var handlerService: HandlerService? = null

    private var myTimerTask: TimerTask? = null
    private lateinit var timer: Timer

    var calendar: Calendar? = null
    var sdf: SimpleDateFormat? = null

    var myViewModel: MyViewModel? = null

    var count: Double = 0.0
    var startTime1: String? = null
    var startTime2: String? = null
    var endTime1: String? = null
    var endTime2: String? = null
    var flag = ""

    var radius1: Double = 0.0
    var radius2: Double = 0.0

    companion object {
        val ACTION_COUNT_BROADCAST: String = MyServiceTimer::class.java.name + "CountBroadcast"
        val EXTRA_TIME = "extra_time"

        var lat1: Double = 0.0
        var lon1: Double = 0.0
        var lat2: Double = 0.0
        var lon2: Double = 0.0

    }

    private inner class HandlerService(looper: Looper): Handler(looper) {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {

            if (myTimerTask == null)
                startTimer()

        }

    }

    private fun resetTimer() {

        if (count != 0.0) {


            if (flag == "First") {
                endTime1 = sdf?.format(calendar?.time)
                myViewModel?.insert(this, DatabaseORMModel(0, flag, startTime1!!, endTime1!!))
                startTime1 = null
            }
            else if (flag == "Second") {
                endTime2 = sdf?.format(calendar?.time)
                myViewModel?.insert(this, DatabaseORMModel(0, flag, startTime2!!, endTime2!!))
                startTime2 = null
            }


        }

        count = 0.0
        sendBroadcastMessage(formatTime(0,0,0))
    }




    private fun startTimer() {
        myTimerTask = object : TimerTask() {
            override fun run() {
                thread {

                    calendar = Calendar.getInstance()
                    sdf = SimpleDateFormat("hh:mm:ss")



                    if ((MyServiceLocation.distance1[0] <= radius1) && (MyServiceLocation.distance1[0] >= 0.1)) {

                        if (count == 0.0)
                            startTime1 = sdf?.format(calendar?.time)

                        count++
                        sendBroadcastMessage(getTimerText())
                        //println(count)

                    }
                    else {
                        if (startTime1 != null) {
                            flag = "First"
                            resetTimer()
                        }

                    }

                    if ((MyServiceLocation.distance2[0] <= radius2) && (MyServiceLocation.distance2[0] >= 0.1)) {

                        if (count == 0.0)
                            startTime2 = sdf?.format(calendar?.time)


                        count++
                        sendBroadcastMessage(getTimerText())
                        //println(count)

                    }
                    else {
                        if (startTime2 != null) {
                            flag = "Second"
                            resetTimer()
                        }

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

    override fun onCreate() {
        super.onCreate()
        val thread = HandlerThread("ServiceStartArgument", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        timer = Timer()
        myViewModel = MyViewModel()

        serviceLooper = thread.looper
        handlerService = HandlerService(serviceLooper!!)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val message: Message = handlerService!!.obtainMessage()
        message.arg1 = startId
        handlerService?.handleMessage(message)

        myViewModel?.selectRadius1(this)?.observe(this, {
            if (it != null) {
                radius1 = it.radius
                lat1 = it.latitude
                lon1 = it.longitude
            }

        })

        myViewModel?.selectRadius2(this)?.observe(this, {
            if (it != null) {
                radius2 = it.radius
                lat2 = it.latitude
                lon2 = it.longitude
            }
        })

        return START_REDELIVER_INTENT
    }

    private fun sendBroadcastMessage(time: String) {
        val intent = Intent(ACTION_COUNT_BROADCAST)
        intent.putExtra(EXTRA_TIME, time)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

}
