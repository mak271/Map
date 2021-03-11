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

    fun selectRadius(context: Context, radius: Double, latitude: Double, longitude: Double): LiveData<CircleModel> {
        return MyRepository.selectRadius(context, radius, latitude, longitude)
    }

}