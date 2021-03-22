package com.example.map

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map.DB.MyViewModel
import com.example.map.DB.ViewModelFactory

class DatabaseActivity: AppCompatActivity() {

    private lateinit var adapter: MyAdapter

    var myViewModel: MyViewModel? = null

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

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            finish()
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_delete).setOnClickListener {
            myViewModel?.deleteAll(this)
        }

        findViewById<Button>(R.id.btn_back_db).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            finish()
            startActivity(intent)
        }

    }

}