package com.example.map.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DAODatabaseORM {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mode: DatabaseORMModel)

    @Query("SELECT * FROM DatabaseORMModel")
    fun selectAll(): LiveData<MutableList<DatabaseORMModel>>

    @Query("DELETE FROM DatabaseORMModel")
    fun deleteAll()

}