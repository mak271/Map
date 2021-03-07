package com.example.map

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

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