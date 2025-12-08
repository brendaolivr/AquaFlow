package com.example.aquaflow.data

import com.example.aquaflow.model.HourlyUsage
import kotlinx.coroutines.delay

object FakeUsageService {

    suspend fun getTodayHourlyUsage(): List<HourlyUsage> {
        // Simule un délai réseau
        delay(500)

        // Données fictives d'aujourd'hui, en litres
        return listOf(
            HourlyUsage("0H", 120),
            HourlyUsage("3H", 80),
            HourlyUsage("6H", 150),
            HourlyUsage("9H", 300),
            HourlyUsage("12H", 420),
            HourlyUsage("15H", 380),
            HourlyUsage("18H", 260),
            HourlyUsage("21H", 190),
            HourlyUsage("24H", 140)
        )
    }
}