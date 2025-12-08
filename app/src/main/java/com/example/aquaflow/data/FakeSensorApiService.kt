package com.example.aquaflow.data

import com.example.aquaflow.model.Sensor
import com.example.aquaflow.model.SensorStatus
import kotlinx.coroutines.delay

class FakeSensorApiService {

    suspend fun getSensors(): List<Sensor> {
        delay(300) // pour simuler un temps réseau

        return listOf(
            Sensor(
                id = "1",
                name = "Capteur principal",
                location = "Chaudière",
                volumeLiters = 15,
                status = SensorStatus.OK,
                lastUpdate = "il y a 2 min"
            ),
            Sensor(
                id = "2",
                name = "Capteur jardin",
                location = "Jardin",
                volumeLiters = 15,
                status = SensorStatus.WARNING,
                lastUpdate = "il y a 5 min"
            ),
            Sensor(
                id = "3",
                name = "Capteur cuisine",
                location = "Cuisine",
                volumeLiters = 15,
                status = SensorStatus.OK,
                lastUpdate = "il y a 1 min"
            ),
            Sensor(
                id = "4",
                name = "Capteur salle de bain",
                location = "Salle de bain",
                volumeLiters = 15,
                status = SensorStatus.ERROR,
                lastUpdate = "il y a 10 min"
            ),
            Sensor(
                id = "5",
                name = "Capteur salle de bain 2",
                location = "Salle de bain 2",
                volumeLiters = 10,
                status = SensorStatus.INACTIF,
                lastUpdate = "Il y a 30 min"
            )
        )
    }
}