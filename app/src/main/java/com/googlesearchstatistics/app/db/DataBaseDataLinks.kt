package com.googlesearchstatistics.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataLink::class], version = 2)
abstract class DataBaseDataLinks : RoomDatabase() {
    abstract fun dataLinksDao(): DataLinksDao
}

object DataBaseBuilder {
    private var instans: DataBaseDataLinks? = null

    fun getInstans(context: Context): DataBaseDataLinks {
        if (instans == null) {
            synchronized(DataBaseDataLinks::class.java) {
                instans = Room.databaseBuilder(
                    context.applicationContext,
                    DataBaseDataLinks::class.java,
                    "DataBaseDataLinks"
                ).build()
            }
        }
        return instans!!
    }

}