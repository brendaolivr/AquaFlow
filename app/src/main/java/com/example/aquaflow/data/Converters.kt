package com.example.aquaflow.data

import androidx.room.TypeConverter
import com.example.aquaflow.model.SensorStatus

class Converters {
    @TypeConverter
    fun fromSensorStatus(status: SensorStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSensorStatus(value: String): SensorStatus {
        return SensorStatus.valueOf(value)
    }
}