package com.example.map.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CircleModel2(@PrimaryKey(autoGenerate = true) val id: Int, val radius: Double, val latitude: Double, val longitude: Double)