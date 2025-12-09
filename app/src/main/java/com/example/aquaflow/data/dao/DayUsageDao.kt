package com.example.aquaflow.data.dao

import androidx.room.*
import com.example.aquaflow.model.DayUsage

@Dao
interface DayUsageDao {
    @Query("SELECT * FROM daily_usage WHERE date = :date")
    suspend fun getUsageForDate(date: String): DayUsage?

    @Query("SELECT * FROM daily_usage ORDER BY date DESC LIMIT 30")
    suspend fun getLast30Days(): List<DayUsage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usages: List<DayUsage>)

    @Query("DELETE FROM daily_usage")
    suspend fun deleteAll()
}