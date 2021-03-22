package com.example.map.DB

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

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

    fun insertCircle(context: Context, model: CircleModel) {
        MyRepository.insertCircle(context, model)
    }

    fun deleteCircle1(context: Context) {
        MyRepository.deleteCircle1(context)
    }

    fun deleteCircle2(context: Context) {
        MyRepository.deleteCircle2(context)
    }

    fun selectCircle1(context: Context): LiveData<CircleModel> {
        return MyRepository.selectCircle1(context)
    }

    fun selectCircle2(context: Context): LiveData<CircleModel> {
        return MyRepository.selectCircle2(context)
    }



}