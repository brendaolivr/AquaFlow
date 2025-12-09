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

        binding.btnMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        binding.btnGoToReports.setOnClickListener {
            (activity as? MainActivity)?.navigateToReports()
        }
        binding.btnGoToSensors.setOnClickListener {
            (activity as? MainActivity)?.navigateToSensors()
        }

        generateRandomData()
    }


    private fun updateTodaySummary(
        today: List<HourlyUsage>,
        yesterday: List<HourlyUsage>
    ) {
        val todayTotal = today.sumOf { it.liters }
        val yesterdayTotal = yesterday.sumOf { it.liters }

        binding.tvTodayVolume.text = todayTotal.toString()

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
                percent < 0 -> AlertStatus.LOW
                else -> AlertStatus.NORMAL
            }
        }

        binding.tvTodaySubtitle.text = "Aujourd'hui  $percentText"

        setAlertStatus(status)
    }

    private enum class AlertStatus { NORMAL, LOW, MEDIUM, HIGH }

    private fun setAlertStatus(status: AlertStatus) {
        val ctx = requireContext()
        val green = ContextCompat.getColor(ctx, R.color.status_good)
        val orange = ContextCompat.getColor(ctx, R.color.status_warning)
        val red = ContextCompat.getColor(ctx, R.color.status_critical)

        val color = when (status) {
            AlertStatus.LOW -> green
            AlertStatus.NORMAL -> green
            AlertStatus.MEDIUM -> orange
            AlertStatus.HIGH -> red
        }


        binding.ivAlert.setColorFilter(color)
        binding.tvTodaySubtitle.setTextColor(color)
    }


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

        // Grouper par tranches de 4 heures
        val groups = listOf(
            "0-3H" to listOf(0, 1, 2, 3),
            "4-7H" to listOf(4, 5, 6, 7),
            "8-11H" to listOf(8, 9, 10, 11),
            "12-15H" to listOf(12, 13, 14, 15),
            "16-19H" to listOf(16, 17, 18, 19),
            "20-23H" to listOf(20, 21, 22, 23)
        )

        data class GroupBar(val label: String, val value: Int)

        val grouped = groups.map { (label, hours) ->
            val sum = data
                .filter { it.hour in hours }
                .sumOf { it.liters }
            GroupBar(label, sum)
        }

        val maxValue = grouped.maxOf { it.value }.coerceAtLeast(1)

        // Lignes pour chaque groupe
        grouped.forEach { entry ->
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
                    (50 * density).toInt(),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = entry.label
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

            val ratio = entry.value.toFloat() / maxValue.toFloat()
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

        setupXAxisVolumeLabels(maxValue)
    }

    private fun generateRandomData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = dateFormat.format(calendar.time)

            db.hourlyUsageDao().deleteUsageForDate(today)
            db.hourlyUsageDao().deleteUsageForDate(yesterday)

            val todayData = (0..23).map { hour ->
                HourlyUsage(
                    date = today,
                    hour = hour,
                    liters = (5..100).random()
                )
            }

            val yesterdayData = (0..23).map { hour ->
                HourlyUsage(
                    date = yesterday,
                    hour = hour,
                    liters = (5..100).random()
                )
            }

            db.hourlyUsageDao().insertAll(todayData)
            db.hourlyUsageDao().insertAll(yesterdayData)

            val todayUsage = db.hourlyUsageDao().getUsageForDate(today)
            val yesterdayUsage = db.hourlyUsageDao().getUsageForDate(yesterday)

            updateTodaySummary(todayUsage, yesterdayUsage)
            setupHorizontalChart(todayUsage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}