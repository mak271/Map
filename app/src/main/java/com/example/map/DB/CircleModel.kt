package com.example.map.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CircleModel(@PrimaryKey val id: Int, val name: String, val radius: Double, val latitude: Double, val longitude: Double)