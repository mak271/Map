package com.example.map

import android.content.Intent
import android.os.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.map.DB.DatabaseORMModel
import com.example.map.DB.MyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
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
    var sdf: DateFormat? = null

    var myViewModel: MyViewModel? = null

    var count: Double = 0.0
    var startTime1: String? = null
    var startTime2: String? = null
    var endTime1: String? = null
    var endTime2: String? = null
    var flag = ""
    lateinit var startDate1: Date
    lateinit var endDate1: Date
    lateinit var startDate2: Date
    lateinit var endDate2: Date

    var name1 = ""
    var name2 = ""

    var diff1 = false
    var diff2 = false

    companion object {
        val ACTION_COUNT_BROADCAST: String = MyServiceTimer::class.java.name + "CountBroadcast"
        val EXTRA_TIME = "extra_time"

        var lat1: Double = 0.0
        var lon1: Double = 0.0
        var lat2: Double = 0.0
        var lon2: Double = 0.0

        var radius1: Double = 0.0
        var radius2: Double = 0.0

    }

    private inner class HandlerService(looper: Looper): Handler(looper) {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {

            if (myTimerTask == null)
                startTimer()

        }

    }

    private fun resetTimer() {

        GlobalScope.launch(Dispatchers.IO) {
            if (count != 0.0) {

                val calendar1: Calendar = Calendar.getInstance()
                val day1 = SimpleDateFormat("EEEE")
                val sdf1 = SimpleDateFormat("dd.MM.y")
                val formatted_day: String = day1.format(calendar1.time)
                val formatted_date: String = sdf1.format(calendar1.time)


                if (flag == "First") {
                    endTime1 = sdf?.format(calendar?.time)
                    endDate1 = sdf!!.parse(endTime1)
                    val diff1 = getTime((endDate1.time - startDate1.time).toInt())
                    withContext(Dispatchers.IO) {
                        myViewModel?.insert(this@MyServiceTimer, DatabaseORMModel(0, name1, startTime1!!, endTime1!!, diff1, formatted_date, formatted_day))
                    }
                    startTime1 = null
                }
                else if (flag == "Second") {
                    endTime2 = sdf?.format(calendar?.time)
                    endDate2 = sdf!!.parse(endTime2)
                    val diff2 = getTime((endDate2.time - startDate2.time).toInt())
                    withContext(Dispatchers.IO) {
                        myViewModel?.insert(this@MyServiceTimer, DatabaseORMModel(0, name2, startTime2!!, endTime2!!, diff2, formatted_date, formatted_day))
                    }
                    startTime2 = null
                }


            }

            count = 0.0
            sendBroadcastMessage(formatTime(0, 0, 0))
        }


    }




    private fun startTimer() {
        myTimerTask = object : TimerTask() {
            override fun run() {
                thread {

                    calendar = Calendar.getInstance()
                    sdf = SimpleDateFormat("hh:mm:ss")

                    //println("${MyServiceLocation.distance1[0]}/${MyServiceLocation.distance2[0]}")




                    if (!diff2) {
                        if ((MyServiceLocation.distance1[0] <= radius1) && (MyServiceLocation.distance1[0] >= 0.1)) {
                            diff1 = true

                            if (count == 0.0) {
                                startTime1 = sdf?.format(calendar?.time)
                                startDate1 = sdf!!.parse(startTime1)
                            }

                            count++
                            sendBroadcastMessage(getTimerText())
                            //println(count)

                        }
                        else {
                            if (startTime1 != null) {
                                flag = "First"
                                resetTimer()
                                diff1 = false
                            }

                        }
                    }



                    if (!diff1) {
                        if ((MyServiceLocation.distance2[0] <= radius2) && (MyServiceLocation.distance2[0] >= 0.1)) {
                            diff2 = true

                            if (count == 0.0) {
                                startTime2 = sdf?.format(calendar?.time)
                                startDate2 = sdf!!.parse(startTime2)
                            }

                            count++
                            sendBroadcastMessage(getTimerText())
                            //println(count)

                        }
                        else {
                            if (startTime2 != null) {
                                flag = "Second"
                                resetTimer()
                                diff2 = false
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

    private fun format(seconds: Int, minutes: Int, hours: Int): String {
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
    }

    private fun getTime(diff: Int): String {

        var rounded = diff/1000

        val hours = rounded / 3600
        rounded -= (hours * 3600)
        val minutes = rounded / 60
        rounded -= (minutes * 60)
        val seconds = rounded

        return format(seconds, minutes, hours)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val message: Message = handlerService!!.obtainMessage()
        message.arg1 = startId
        handlerService?.handleMessage(message)

        myViewModel?.selectCircle1(this)?.observe(this, {
            if (it != null) {
                radius1 = it.radius
                lat1 = it.latitude
                lon1 = it.longitude
                name1 = it.name
            } else {
                radius1 = 0.0
                lat1 = 0.0
                lon1 = 0.0
            }

        })

        myViewModel?.selectCircle2(this)?.observe(this, {
            if (it != null) {
                radius2 = it.radius
                lat2 = it.latitude
                lon2 = it.longitude
                name2 = it.name
            } else {
                radius2 = 0.0
                lat2 = 0.0
                lon2 = 0.0
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
