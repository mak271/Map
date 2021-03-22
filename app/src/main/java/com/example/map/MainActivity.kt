package com.example.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.example.map.DB.DatabaseORMModel
import com.example.map.DB.MyViewModel
import com.example.map.DB.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.*
import me.relex.circleindicator.CircleIndicator3
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.seconds


class MainActivity: AppCompatActivity() {

    private var tv_timer: TextView? = null

    private var myViewModel: MyViewModel? = null
    private var series1: LineGraphSeries<DataPoint>? = null
    private var series2: LineGraphSeries<DataPoint>? = null

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var adapter: ViewPagerAdapter
    private lateinit var view_pager: ViewPager2

    //private lateinit var binding: ResultProfileBinding

    companion object {
        var points1: Array<DataPoint?> = emptyArray()
        var points2: Array<DataPoint?> = emptyArray()
    }

    @SuppressLint("RestrictedApi", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_timer = findViewById(R.id.tv_timer_main)

        if (startService(Intent(this, MyServiceTimer::class.java)) == null)
            startService(Intent(this, MyServiceTimer::class.java))

        findViewById<Button>(R.id.btn_main_map).setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        findViewById<Button>(R.id.btn_main_other).setOnClickListener {
            startActivity(Intent(this, DatabaseActivity::class.java))
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val result = intent?.getStringExtra(MyServiceTimer.EXTRA_TIME)
                    tv_timer?.text = result
                }
            }, IntentFilter(MyServiceTimer.ACTION_COUNT_BROADCAST)
        )

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        updateLocation()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(this@MainActivity, "You need accept", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {

                    }

                }).check()


        GlobalScope.launch(Dispatchers.Main) {
            myViewModel = ViewModelProvider(this@MainActivity, ViewModelFactory()).get(MyViewModel::class.java)
            myViewModel?.selectAll(this@MainActivity)?.observe(this@MainActivity, {
                series1 = LineGraphSeries<DataPoint>(weekGraph(it))
                series2 = LineGraphSeries<DataPoint>(monthGraph(it))
                customizeSeries()
            })
            delay(100L)
            createViewPager()
        }

    }



    private fun getList(): MutableList<LineGraphSeries<DataPoint>> {
        val list = mutableListOf<LineGraphSeries<DataPoint>>()
        list.add(series1!!)
        list.add(series2!!)
        return list
    }



    private fun updateLocation() {
        buildLocationRequest()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
            return

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MyServiceLocation::class.java)
        intent.action = MyServiceLocation.ACTION_PROCESS_UPDATE
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 500
    }

    private fun weekGraph(it: MutableList<DatabaseORMModel>): Array<DataPoint?> {
        points1 = arrayOfNulls(7)
        var todayAsString: String
        var k: Double
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -6)
        val calendar1 = Calendar.getInstance()
        calendar1.add(Calendar.DATE, 1)
        val calendar2 = Calendar.getInstance()
        calendar2.add(Calendar.DATE, -6)
        for (i in 0..6) {
            val dayOfWeek: Date = calendar.time
            calendar.add(Calendar.DATE, 1)
            val df: DateFormat = SimpleDateFormat("EEE", Locale.US)
            todayAsString = df.format(dayOfWeek)
            val list = mutableListOf<Double>()
            for (z in 0 until it.size) {
                if ((todayAsString == it[z].dayOfWeek.substring(0, 3)) && ((calendar.time <= calendar1.time) && (calendar.time >= calendar2.time))) {
                    val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                    try {
                        val myDate: Date? = sdf.parse(it[z].time)
                        val timeInMilliseconds: Long = myDate!!.time
                        val m = timeInMilliseconds.toDouble()
                        k = m / 1000 / 3600
                        list.add(k)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            var listItem = 0.0
            for (l in 0 until list.size) {
                listItem += list[l]
            }
            points1[i] = DataPoint(dayOfWeek, listItem)
        }
        return points1
    }

    private fun monthGraph(it: MutableList<DatabaseORMModel>): Array<DataPoint?> {
        points2 = arrayOfNulls(31)
        var todayAsString: String
        var k: Double
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -30)
        val calendar1 = Calendar.getInstance()
        calendar1.add(Calendar.DATE, 1)
        val calendar2 = Calendar.getInstance()
        calendar2.add(Calendar.DATE, -30)
        for (i in 0..30) {
            val dayOfMonth: Date = calendar.time
            calendar.add(Calendar.DATE, 1)
            val df: DateFormat = SimpleDateFormat("dd.MM.y", Locale.US)
            todayAsString = df.format(dayOfMonth)
            val list = mutableListOf<Double>()
            for (z in 0 until it.size) {
                if ((todayAsString == it[z].date) && ((calendar.time <= calendar1.time) && (calendar.time >= calendar2.time))) {
                    val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                    try {
                        val myDate: Date? = sdf.parse(it[z].time)
                        val timeInMilliseconds: Long = myDate!!.time
                        val m = timeInMilliseconds.toDouble()
                        k = m / 1000 / 3600
                        list.add(k)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            var listItem = 0.0
            for (l in 0 until list.size) {
                listItem += list[l]
            }
            points2[i] = DataPoint(dayOfMonth, listItem)
        }
        return points2
    }

    private fun customizeSeries() {

        series1!!.thickness = 5
        series1!!.isDrawBackground = true
        series1!!.backgroundColor = R.color.teal_200
        series1!!.color = R.color.teal_700
        series1!!.setOnDataPointTapListener { _, dataPoint ->
            val dayOfMonth: Double = dataPoint.x
            val df: DateFormat = SimpleDateFormat("EEEE", Locale.US)
            val todayAsString = df.format(dayOfMonth)
            if (dataPoint.y > 0)
                dataPoint.y.toString().substring(0,3)
            Toast.makeText(this@MainActivity, "Пробыл: ${dataPoint.y} часа. День недели: $todayAsString", Toast.LENGTH_SHORT).show()
        }
        series2!!.thickness = 5
        series2!!.isDrawBackground = true
        series2!!.backgroundColor = R.color.teal_200
        series2!!.color = R.color.teal_700
        series2!!.setOnDataPointTapListener { _, dataPoint ->

            val dayOfMonth: Double = dataPoint.x
            val df: DateFormat = SimpleDateFormat("dd.MM.y", Locale.US)
            val todayAsString = df.format(dayOfMonth)

            Toast.makeText(this@MainActivity, "Пробыл: ${dataPoint.y} часа. Дата: $todayAsString", Toast.LENGTH_SHORT).show()
        }
    }


    private fun createViewPager() {
        adapter = ViewPagerAdapter()
        adapter.initList(getList())
        view_pager = findViewById(R.id.view_pager2)
        view_pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        view_pager.adapter = adapter
        val indicator: CircleIndicator3 = findViewById(R.id.indicator)
        indicator.setViewPager(view_pager)
    }

}