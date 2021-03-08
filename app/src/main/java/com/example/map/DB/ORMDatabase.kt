package com.example.map.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DatabaseORMModel::class], version = 1)
abstract class ORMDatabase: RoomDatabase() {

    abstract fun daoDatabaseORM(): DAODatabaseORM

    companion object {
        var db: ORMDatabase? = null

        fun getORMDatabase(context: Context): ORMDatabase? {
            if (db == null) {
                synchronized(ORMDatabase::class) {
                    db = Room.databaseBuilder(context, ORMDatabase::class.java, "DatabaseMap").build()
                }
            }
            return db
        }
    }

}