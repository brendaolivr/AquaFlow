package com.example.aquaflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.aquaflow.data.dao.DayUsageDao
import com.example.aquaflow.data.dao.HourlyUsageDao
import com.example.aquaflow.data.dao.SensorDao
import com.example.aquaflow.model.DayUsage
import com.example.aquaflow.model.HourlyUsage
import com.example.aquaflow.model.Sensor

@Database(
    entities = [Sensor::class, HourlyUsage::class, DayUsage::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sensorDao(): SensorDao
    abstract fun hourlyUsageDao(): HourlyUsageDao
    abstract fun dayUsageDao(): DayUsageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aquaflow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}