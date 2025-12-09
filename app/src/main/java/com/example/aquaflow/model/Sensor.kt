package com.example.aquaflow.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "sensors")
data class Sensor(
    @PrimaryKey
    val id: String,
    val name: String,
    val location: String,
    val volumeLiters: Int,
    val status: SensorStatus,
    val lastUpdate: String
) {
    @get:Ignore
    val statusText: String
        get() = when (status) {
            SensorStatus.OK -> "Actif"
            SensorStatus.WARNING -> "Avertissement"
            SensorStatus.ERROR -> "Erreur"
            SensorStatus.INACTIF -> "Inactif"
        }
}

enum class SensorStatus {
    OK, WARNING, ERROR, INACTIF
}