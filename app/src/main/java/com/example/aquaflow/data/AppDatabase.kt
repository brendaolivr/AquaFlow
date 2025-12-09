package com.example.aquaflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aquaflow.data.dao.DayUsageDao
import com.example.aquaflow.data.dao.HourlyUsageDao
import com.example.aquaflow.data.dao.SensorDao
import com.example.aquaflow.model.DayUsage
import com.example.aquaflow.model.HourlyUsage
import com.example.aquaflow.model.Sensor
import com.example.aquaflow.model.SensorStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Database(
    entities = [Sensor::class, HourlyUsage::class, DayUsage::class],
    version = 2,
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
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    prepopulateDatabase(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(database: AppDatabase) {
            val sensorDao = database.sensorDao()
            val hourlyUsageDao = database.hourlyUsageDao()
            val dayUsageDao = database.dayUsageDao()

            val sensors = listOf(
                Sensor(
                    id = "1",
                    name = "Capteur Cuisine",
                    location = "Cuisine",
                    volumeLiters = 120,
                    status = SensorStatus.OK,
                    lastUpdate = "Il y a 5 min"
                ),
                Sensor(
                    id = "2",
                    name = "Capteur Salle de bain",
                    location = "Salle de bain",
                    volumeLiters = 85,
                    status = SensorStatus.WARNING,
                    lastUpdate = "Il y a 12 min"
                ),
                Sensor(
                    id = "3",
                    name = "Capteur Jardin",
                    location = "Jardin",
                    volumeLiters = 200,
                    status = SensorStatus.ERROR,
                    lastUpdate = "Il y a 1h"
                ),
                Sensor(
                    id = "4",
                    name = "Capteur Garage",
                    location = "Garage",
                    volumeLiters = 50,
                    status = SensorStatus.OK,
                    lastUpdate = "Il y a 3 min"
                ),
                Sensor(
                    id = "5",
                    name = "Capteur Cave",
                    location = "Cave",
                    volumeLiters = 0,
                    status = SensorStatus.INACTIF,
                    lastUpdate = "Jamais"
                )
            )
            sensorDao.insertAll(sensors)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())

            val hourlyData = (0..23).map { hour ->
                HourlyUsage(
                    date = today,
                    hour = hour,
                    liters = (10..40).random()
                )
            }
            hourlyUsageDao.insertAll(hourlyData)

            val calendar = Calendar.getInstance()
            val dailyData = mutableListOf<DayUsage>()

            for (i in 0 until 30) {
                val date = dateFormat.format(calendar.time)
                val totalLiters = (200..600).random()

                dailyData.add(
                    DayUsage(
                        date = date,
                        totalLiters = totalLiters
                    )
                )

                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }

            dayUsageDao.insertAll(dailyData)
        }
    }
}