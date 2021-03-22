package com.example.map.DB

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
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

        fun insertCircle(context: Context, model: CircleModel) {
            ormDatabase = initDB(context)
            GlobalScope.launch(Dispatchers.IO) {
                ormDatabase!!.daoDatabaseORM().insertCircle(model)
            }
        }

        fun deleteCircle1(context: Context) {
            ormDatabase = initDB(context)
            GlobalScope.launch(Dispatchers.IO) {
                ormDatabase!!.daoDatabaseORM().deleteCircle1()
            }
        }

        fun deleteCircle2(context: Context) {
            ormDatabase = initDB(context)
            GlobalScope.launch(Dispatchers.IO) {
                ormDatabase!!.daoDatabaseORM().deleteCircle2()
            }
        }

        fun selectCircle1(context: Context): LiveData<CircleModel> {
            ormDatabase = initDB(context)
            return ormDatabase!!.daoDatabaseORM().selectCircle1()
        }

        fun selectCircle2(context: Context): LiveData<CircleModel> {
            ormDatabase = initDB(context)
            return ormDatabase!!.daoDatabaseORM().selectCircle2()
        }


    }

}