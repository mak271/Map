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

    @Query("SELECT * FROM CircleModel WHERE radius = :radius AND latitude = :latitude AND longitude = :longitude")
    fun selectRadius(radius: Double, latitude: Double, longitude: Double): LiveData<CircleModel>

}