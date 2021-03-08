package com.example.map.DB

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.map.DB.DatabaseORMModel
import com.example.map.DB.MyRepository

class MyViewModel(): ViewModel() {

    fun insert(context: Context, model: DatabaseORMModel) {
        MyRepository.insert(context, model)
    }

    fun selectAll(context: Context): LiveData<MutableList<DatabaseORMModel>> {
        return MyRepository.selectAll(context)
    }

    fun deleteAll(context: Context) {
        MyRepository.deleteAll(context)
    }

}