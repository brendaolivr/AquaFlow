package com.example.aquaflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_usage")
data class HourlyUsage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hour: Int,
    val liters: Int,
    val date: String
)