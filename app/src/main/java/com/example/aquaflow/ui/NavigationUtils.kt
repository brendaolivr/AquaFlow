package com.example.aquaflow.ui

import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.aquaflow.R
import com.example.aquaflow.ui.home.HomeFragment
import com.example.aquaflow.ui.reports.ReportsFragment
import com.example.aquaflow.ui.sensors.SensorsFragment

fun Fragment.showTopMenu(anchor: View) {
    val popup = PopupMenu(requireContext(), anchor)
    popup.menuInflater.inflate(R.menu.top_menu, popup.menu)

    popup.setOnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.menu_home -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
            R.id.menu_reports -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ReportsFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
            R.id.menu_sensors -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SensorsFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
            else -> false
        }
    }

    popup.show()
}