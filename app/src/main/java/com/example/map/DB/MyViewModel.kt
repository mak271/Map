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

    fun insertCircle1(context: Context, model: CircleModel1) {
        MyRepository.insertCircle1(context, model)
    }

    fun selectRadius1(context: Context): LiveData<CircleModel1> {
        return MyRepository.selectRadius1(context)
    }

    fun insertCircle2(context: Context, model: CircleModel2) {
        MyRepository.insertCircle2(context, model)
    }

    fun selectRadius2(context: Context): LiveData<CircleModel2> {
        return MyRepository.selectRadius2(context)
    }

}