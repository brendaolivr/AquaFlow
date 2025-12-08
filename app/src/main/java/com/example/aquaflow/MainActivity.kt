package com.example.aquaflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import com.example.aquaflow.databinding.ActivityMainBinding
import com.example.aquaflow.ui.home.HomeFragment
import com.example.aquaflow.ui.reports.ReportsFragment
import com.example.aquaflow.ui.sensors.SensorsFragment
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}