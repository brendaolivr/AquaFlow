package com.example.aquaflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.example.aquaflow.databinding.ActivityMainBinding
import com.example.aquaflow.data.AppDatabase
import com.example.aquaflow.model.*
import com.example.aquaflow.ui.home.HomeFragment
import com.example.aquaflow.ui.reports.ReportsFragment
import com.example.aquaflow.ui.sensors.SensorsFragment
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            if (db.sensorDao().getAllSensors().isEmpty()) {
                populateMockData(db)
            }
        }

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                // Si le menu latéral est ouvert, on le ferme
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }

        setupDrawerNavigation()

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_reports -> {
                    replaceFragment(ReportsFragment())
                    true
                }
                R.id.nav_sensors -> {
                    replaceFragment(SensorsFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun setupDrawerNavigation() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navigateToHome()
                R.id.nav_reports -> navigateToReports()
                R.id.nav_sensors -> navigateToSensors()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun navigateToHome() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        replaceFragment(HomeFragment())
    }

    fun navigateToReports() {
        binding.bottomNavigation.selectedItemId = R.id.nav_reports
        replaceFragment(ReportsFragment())
    }

    fun navigateToSensors() {
        binding.bottomNavigation.selectedItemId = R.id.nav_sensors
        replaceFragment(SensorsFragment())
    }

    private suspend fun populateMockData(db: AppDatabase) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val twoDaysAgo = dateFormat.format(calendar.time)

        val sensors = listOf(
            Sensor(
                id = "sensor_1",
                name = "Chaudière",
                location = "Sous-sol",
                volumeLiters = 45,
                lastUpdate = "15:30",
                status = SensorStatus.OK
            ),
            Sensor(
                id = "sensor_2",
                name = "Jardin",
                location = "Extérieur",
                volumeLiters = 120,
                lastUpdate = "14:00",
                status = SensorStatus.WARNING
            ),
            Sensor(
                id = "sensor_3",
                name = "Cuisine",
                location = "Rez-de-chaussée",
                volumeLiters = 30,
                lastUpdate = "16:00",
                status = SensorStatus.OK
            ),
            Sensor(
                id = "sensor_4",
                name = "Salle de bain",
                location = "Étage",
                volumeLiters = 80,
                lastUpdate = "12:45",
                status = SensorStatus.ERROR
            )
        )
        db.sensorDao().insertAll(sensors)

        val hourlyUsagesToday = listOf(
            HourlyUsage(hour = 0, liters = 2, date = today),
            HourlyUsage(hour = 3, liters = 1, date = today),
            HourlyUsage(hour = 6, liters = 15, date = today),
            HourlyUsage(hour = 9, liters = 25, date = today),
            HourlyUsage(hour = 12, liters = 30, date = today),
            HourlyUsage(hour = 15, liters = 20, date = today),
            HourlyUsage(hour = 18, liters = 35, date = today),
            HourlyUsage(hour = 21, liters = 18, date = today)
        )
        db.hourlyUsageDao().insertAll(hourlyUsagesToday)

        val dailyUsages = listOf(
            DayUsage(date = today, totalLiters = 146),
            DayUsage(date = yesterday, totalLiters = 130),
            DayUsage(date = twoDaysAgo, totalLiters = 155)
        )
        db.dayUsageDao().insertAll(dailyUsages)
    }
}