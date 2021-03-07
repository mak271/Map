package com.example.map

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseORMModel(@PrimaryKey val start: String, val end: String)
