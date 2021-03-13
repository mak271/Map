package com.example.map.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseORMModel(@PrimaryKey(autoGenerate = true) val id: Int, val name: String, val start: String, val end: String)
