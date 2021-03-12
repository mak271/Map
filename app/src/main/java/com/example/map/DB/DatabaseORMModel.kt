package com.example.map.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseORMModel(val name: String, @PrimaryKey val start: String, val end: String)
