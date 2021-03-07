package com.example.map

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DAODatabaseORM {

    @Insert
    fun insert(mode: DatabaseORMModel)

    @Query("SELECT * FROM DatabaseORMModel")
    fun selectAll(): LiveData<MutableList<DatabaseORMModel>>

    @Query("DELETE FROM DatabaseORMModel")
    fun deleteAll()

}