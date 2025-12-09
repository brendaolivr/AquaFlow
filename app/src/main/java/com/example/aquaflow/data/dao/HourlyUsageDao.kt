package com.example.aquaflow.data.dao

import androidx.room.*
import com.example.aquaflow.model.HourlyUsage

@Dao
interface HourlyUsageDao {
    @Query("SELECT * FROM hourly_usage WHERE date = :date ORDER BY hour ASC")
    suspend fun getUsageForDate(date: String): List<HourlyUsage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usages: List<HourlyUsage>)

    @Query("DELETE FROM hourly_usage")
    suspend fun deleteAll()

    @Query("DELETE FROM hourly_usage WHERE date = :date")
    suspend fun deleteUsageForDate(date: String)
}