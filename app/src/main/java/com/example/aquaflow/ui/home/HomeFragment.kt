package com.example.aquaflow.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aquaflow.MainActivity
import com.example.aquaflow.R
import com.example.aquaflow.data.AppDatabase
import com.example.aquaflow.databinding.FragmentHomeBinding
import com.example.aquaflow.model.HourlyUsage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())

        // Menu
        binding.btnMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        // Boutons de navigation
        binding.btnGoToReports.setOnClickListener {
            (activity as? MainActivity)?.navigateToReports()
        }
        binding.btnGoToSensors.setOnClickListener {
            (activity as? MainActivity)?.navigateToSensors()
        }

        // Charger données depuis Room
        viewLifecycleOwner.lifecycleScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = dateFormat.format(calendar.time)

            val todayUsage = db.hourlyUsageDao().getUsageForDate(today)
            val yesterdayUsage = db.hourlyUsageDao().getUsageForDate(yesterday)

            updateTodaySummary(todayUsage, yesterdayUsage)
            setupHorizontalChart(todayUsage)
        }
    }

    //Résumé "Aujourd'hui"

    private fun updateTodaySummary(
        today: List<HourlyUsage>,
        yesterday: List<HourlyUsage>
    ) {
        val todayTotal = today.sumOf { it.liters }
        val yesterdayTotal = yesterday.sumOf { it.liters }

        // Affiche le volume d'aujourd'hui
        binding.tvTodayVolume.text = todayTotal.toString()

        // Calcul du pourcentage vs hier
        val percentText: String
        val status: AlertStatus

        if (yesterdayTotal == 0) {
            percentText = "0.0%"
            status = AlertStatus.NORMAL
        } else {
            val diff = todayTotal - yesterdayTotal
            val percent = diff.toDouble() / yesterdayTotal.toDouble() * 100.0
            val rounded = (percent * 10).roundToInt() / 10.0 // X.X %

            percentText = if (rounded >= 0) {
                "+${rounded}%"
            } else {
                "${rounded}%"
            }

            status = when {
                percent >= 50.0 -> AlertStatus.HIGH
                percent >= 20.0 -> AlertStatus.MEDIUM
                percent <= -20.0 -> AlertStatus.LOW
                else -> AlertStatus.NORMAL
            }
        }

        // Met à jour le sous-titre
        binding.tvTodaySubtitle.text = "Aujourd'hui  $percentText"

        // Met à jour la couleur de l'alerte
        setAlertStatus(status)
    }

    private enum class AlertStatus { NORMAL, LOW, MEDIUM, HIGH }

    private fun setAlertStatus(status: AlertStatus) {
        val ctx = requireContext()
        val green = ContextCompat.getColor(ctx, R.color.status_good)
        val orange = ContextCompat.getColor(ctx, R.color.status_warning)
        val red = ContextCompat.getColor(ctx, R.color.status_critical)

        val color = when (status) {
            AlertStatus.NORMAL -> green
            AlertStatus.LOW -> orange
            AlertStatus.MEDIUM -> orange
            AlertStatus.HIGH -> red
        }

        // Teinte l'icône d'alerte et la couleur du texte sous-titre
        binding.ivAlert.setColorFilter(color)
        binding.tvTodaySubtitle.setTextColor(color)
    }

    //Graphique "Activité d'aujourd'hui"

    private fun setupXAxisVolumeLabels(maxValue: Int) {
        val context = requireContext()
        val density = context.resources.displayMetrics.density
        val gray = ContextCompat.getColor(context, R.color.gray_dark)

        val container = binding.layoutChartXAxisLabels
        container.removeAllViews()
        container.orientation = LinearLayout.HORIZONTAL
        container.gravity = Gravity.CENTER

        if (maxValue <= 0) return

        val step = when {
            maxValue <= 20 -> 5
            maxValue <= 50 -> 10
            maxValue <= 100 -> 20
            else -> (maxValue / 5).coerceAtLeast(20)
        }

        val labels = mutableListOf<Int>()
        var v = 0
        while (v <= maxValue) {
            labels.add(v)
            v += step
        }

        if (labels.last() < maxValue) {
            labels.add(maxValue)
        }

        labels.forEach { value ->
            val tv = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                gravity = Gravity.CENTER
                text = "${value}L"
                textSize = 12f
                setTextColor(gray)
            }
            container.addView(tv)
        }
    }

    private fun setupHorizontalChart(data: List<HourlyUsage>) {
        val context = requireContext()
        val container = binding.layoutChartBars
        container.removeAllViews()

        if (data.isEmpty()) return

        val density = context.resources.displayMetrics.density
        val blue = ContextCompat.getColor(context, R.color.primary_blue)
        val gray = ContextCompat.getColor(context, R.color.gray_dark)

        val sorted = data.sortedBy { it.hour }

        val maxValue = sorted.maxOf { it.liters }.coerceAtLeast(1)

        // Lignes heure
        sorted.forEach { entry ->
            val row = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (4 * density).toInt()
                    bottomMargin = (4 * density).toInt()
                }
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val tvHour = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (40 * density).toInt(),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = "${entry.hour}H"
                textSize = 12f
                setTextColor(gray)
            }

            val barContainer = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginStart = (4 * density).toInt()
                }
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val ratio = entry.liters.toFloat() / maxValue.toFloat()
            val maxWidthPx = (220 * density).toInt()
            val barWidthPx = (maxWidthPx * ratio).toInt().coerceAtLeast((4 * density).toInt())

            val bar = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    barWidthPx,
                    (4 * density).toInt()
                )
                setBackgroundColor(blue)
            }

            barContainer.addView(bar)

            row.addView(tvHour)
            row.addView(barContainer)
            container.addView(row)
        }

        // Graduations de volume (0L, 5L, 10L, ...)
        setupXAxisVolumeLabels(maxValue)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}