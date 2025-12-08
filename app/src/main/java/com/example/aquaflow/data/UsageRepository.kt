package com.example.aquaflow.data

import com.example.aquaflow.model.HourlyUsage
import com.example.aquaflow.model.DayUsage

interface UsageRepository {
    suspend fun getTodayUsage(): List<HourlyUsage>
    suspend fun getYesterdayUsage(): List<HourlyUsage>
    suspend fun getWeekUsage(): List<HourlyUsage>
    suspend fun getMonthUsage(): List<HourlyUsage>
    suspend fun getWeekDailyUsage(): List<DayUsage>
    suspend fun getMonthDailyUsage(): List<DayUsage>
}