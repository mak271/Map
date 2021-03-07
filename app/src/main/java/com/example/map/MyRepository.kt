package com.example.map

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyRepository {

    companion object {
        var ormDatabase: ORMDatabase? = null

        private fun initDB(context: Context): ORMDatabase {
            return ORMDatabase.getORMDatabase(context)!!
        }

        fun insert(context: Context, model: DatabaseORMModel) {
            ormDatabase = initDB(context)
            GlobalScope.launch(Dispatchers.IO) {
                ormDatabase!!.daoDatabaseORM().insert(model)
            }
        }

        fun selectAll(context: Context): LiveData<MutableList<DatabaseORMModel>> {
            ormDatabase = initDB(context)
            return ormDatabase!!.daoDatabaseORM().selectAll()
        }

        fun deleteAll(context: Context) {
            ormDatabase = initDB(context)
            GlobalScope.launch(Dispatchers.IO) {
                ormDatabase!!.daoDatabaseORM().deleteAll()
            }
        }

    }

}