package com.example.aquaflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_usage")
data class HourlyUsage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hour: Int,        // 0–23
    val liters: Int,
    val date: String      // format "2025-12-09"
)