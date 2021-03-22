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
    fun insertCircle(mode: CircleModel)

    @Query("DELETE FROM CircleModel WHERE id=0")
    fun deleteCircle1()

    @Query("DELETE FROM CircleModel WHERE id=1")
    fun deleteCircle2()

    @Query("SELECT * FROM CircleModel WHERE id=0")
    fun selectCircle1(): LiveData<CircleModel>

    @Query("SELECT * FROM CircleModel WHERE id=1")
    fun selectCircle2(): LiveData<CircleModel>

}