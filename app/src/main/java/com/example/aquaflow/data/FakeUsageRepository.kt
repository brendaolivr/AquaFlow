package com.example.aquaflow.data

import com.example.aquaflow.model.HourlyUsage
import com.example.aquaflow.model.DayUsage
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeUsageRepository : UsageRepository {

    private val hours = listOf("0H", "3H", "6H", "9H", "12H", "15H", "18H", "21H", "24H")

    override suspend fun getTodayUsage(): List<HourlyUsage> {
        delay(200)
        return hours.map { h ->
            HourlyUsage(h, Random.nextInt(50, 250))
        }
    }

    override suspend fun getYesterdayUsage(): List<HourlyUsage> {
        delay(150)
        return hours.map { h ->
            HourlyUsage(h, Random.nextInt(40, 220))
        }
    }

    override suspend fun getWeekUsage(): List<HourlyUsage> {
        delay(200)
        return hours.map { h ->
            HourlyUsage(h, Random.nextInt(80, 300))
        }
    }

    override suspend fun getMonthUsage(): List<HourlyUsage> {
        delay(200)
        return hours.map { h ->
            HourlyUsage(h, Random.nextInt(60, 260))
        }
    }

    override suspend fun getWeekDailyUsage(): List<DayUsage> = listOf(
        DayUsage("Lun", 120),
        DayUsage("Mar", 150),
        DayUsage("Mer", 90),
        DayUsage("Jeu", 180),
        DayUsage("Ven", 130),
        DayUsage("Sam", 160),
        DayUsage("Dim", 110),
    )

    override suspend fun getMonthDailyUsage(): List<DayUsage> {
        val daysCount = 30

        val base = 100

        val result = mutableListOf<DayUsage>()
        for (i in 1..daysCount) {
            val variation = when {
                i % 7 == 0 -> 40
                i % 5 == 0 -> -20
                i % 3 == 0 -> 15
                else -> 0
            }

            val volume = (base + variation).coerceAtLeast(30)
            val label = "Jour $i"

            result.add(DayUsage(dayLabel = label, volumeLiters = volume))
        }
        return result
    }
}