package com.example.aquaflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_usage")
data class DayUsage(
    @PrimaryKey
    val date: String,     // "2025-12-09"
    val totalLiters: Int
)