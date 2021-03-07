package com.example.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DatabaseActivity: AppCompatActivity() {

    private lateinit var adapter: MyAdapter
    private var myViewModel: MyViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)
        adapter = MyAdapter()

        val recyclerView: RecyclerView = findViewById(R.id.rcView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        myViewModel = ViewModelProvider(this, ViewModelFactory()).get(MyViewModel::class.java)
        myViewModel?.selectAll(this)?.observe(this, Observer {
            adapter.initList(it)
        })

        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val start = intent?.getStringExtra(MyServiceTimer.EXTRA_START)
                    val end = intent?.getStringExtra(MyServiceTimer.EXTRA_END)

                    //myViewModel?.insert(this@DatabaseActivity, DatabaseORMModel(start.toString(), end.toString()))

                }
            }, IntentFilter(MyServiceTimer.ACTION_TIME_BROADCAST)
        )

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        findViewById<Button>(R.id.btn_delete).setOnClickListener {
            myViewModel?.deleteAll(this)
        }

    }

}