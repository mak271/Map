package com.example.map.DB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.android.gms.maps.model.LatLng

@Dao
interface DAODatabaseORM {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mode: DatabaseORMModel)

    @Query("SELECT * FROM DatabaseORMModel")
    fun selectAll(): LiveData<MutableList<DatabaseORMModel>>

    @Query("DELETE FROM DatabaseORMModel")
    fun deleteAll()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCircle1(mode: CircleModel1): Long

    @Query("SELECT * FROM CircleModel1 ORDER BY id DESC LIMIT 1")
    fun selectRadius1(): LiveData<CircleModel1>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCircle2(mode: CircleModel2): Long

    @Query("SELECT * FROM CircleModel2 ORDER BY id DESC LIMIT 1")
    fun selectRadius2(): LiveData<CircleModel2>

}