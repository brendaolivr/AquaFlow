package com.example.aquaflow.data.dao

import androidx.room.*
import com.example.aquaflow.model.Sensor

@Dao
interface SensorDao {
    @Query("SELECT * FROM sensors")
    suspend fun getAllSensors(): List<Sensor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sensors: List<Sensor>)

    @Query("DELETE FROM sensors")
    suspend fun deleteAll()
}