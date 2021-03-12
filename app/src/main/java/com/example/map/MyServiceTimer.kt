package com.example.map

import android.app.Service
import android.content.Intent
import android.os.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.map.DB.DatabaseORMModel
import com.example.map.DB.MyViewModel
import com.example.map.DatabaseActivity.Companion.myViewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class MyServiceTimer : Service() {

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

    companion object {
        val ACTION_COUNT_BROADCAST: String = MyServiceTimer::class.java.name + "CountBroadcast"
        val EXTRA_TIME = "extra_time"

        var dist1: Float = 0.0F
        var dist2: Float = 0.0f
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

            myViewModel = MyViewModel()
            if (flag == "First") {
                endTime1 = sdf?.format(calendar?.time)
                myViewModel?.insert(this, DatabaseORMModel(flag, startTime1!!, endTime1!!))
                startTime1 = null
            }
            else if (flag == "Second") {
                endTime2 = sdf?.format(calendar?.time)
                myViewModel?.insert(this, DatabaseORMModel(flag, startTime2!!, endTime2!!))
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



                    if (MapsActivity.rad1 != null) {

                        if ((MyServiceLocation.distance1[0] <= MapsActivity.rad1!!) && (MyServiceLocation.distance1[0] >= 0.1)) {

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

                    }

                    if (MapsActivity.rad2 != null) {

                        if ((MyServiceLocation.distance2[0] <= MapsActivity.rad2!!) && (MyServiceLocation.distance2[0] >= 0.1)) {

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

        return START_REDELIVER_INTENT
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

}
