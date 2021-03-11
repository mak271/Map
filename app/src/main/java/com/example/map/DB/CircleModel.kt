package com.example.map.DB

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng

@Entity
data class CircleModel(@PrimaryKey val radius: Double, val latitude: Double, val longitude: Double)

