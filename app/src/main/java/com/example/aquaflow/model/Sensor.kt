package com.example.aquaflow.model

data class Sensor(
    val id: String,
    val name: String,
    val location: String,
    val volumeLiters: Int,
    val status: SensorStatus,
    val lastUpdate: String
) {
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