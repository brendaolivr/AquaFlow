package com.example.aquaflow.ui.reports

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
import com.example.aquaflow.data.FakeUsageRepository
import com.example.aquaflow.data.UsageRepository
import com.example.aquaflow.databinding.FragmentsReportsBinding
import com.example.aquaflow.model.DayUsage
import com.example.aquaflow.model.HourlyUsage
import com.example.aquaflow.ui.showTopMenu
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ReportsFragment : Fragment() {

    private var _binding: FragmentsReportsBinding? = null
    private val binding get() = _binding!!

    private val usageRepository: UsageRepository = FakeUsageRepository()

    private enum class TabPeriod { TODAY, THIS_WEEK, THIS_MONTH }
    private var currentTab: TabPeriod = TabPeriod.TODAY

    private var todayHourly: List<HourlyUsage> = emptyList()
    private var weekDaily: List<DayUsage> = emptyList()
    private var monthDaily: List<DayUsage> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentsReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menu du haut
        binding.btnMenuReports.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        // Retour vers Home
        binding.btnBackReports.setOnClickListener {
            (activity as? MainActivity)?.navigateToHome()
        }

        highlightTab(TabPeriod.TODAY)

        // Charger les données simulées
        viewLifecycleOwner.lifecycleScope.launch {
            todayHourly = usageRepository.getTodayUsage()
            weekDaily = usageRepository.getWeekDailyUsage()
            monthDaily = usageRepository.getMonthDailyUsage()

            // Mettre à jour les résumés
            updateSummaries()

            // Dessiner l'histogramme
            drawCurrentTabChart()
        }

        // Onglets
        binding.tabToday.setOnClickListener {
            currentTab = TabPeriod.TODAY
            highlightTab(currentTab)
            drawCurrentTabChart()
        }

        binding.tabThisWeek.setOnClickListener {
            currentTab = TabPeriod.THIS_WEEK
            highlightTab(currentTab)
            drawCurrentTabChart()
        }

        binding.tabThisMonth.setOnClickListener {
            currentTab = TabPeriod.THIS_MONTH
            highlightTab(currentTab)
            drawCurrentTabChart()
        }
    }

    //Résumés en haut (volume moyen, alertes, gaspillage, total litres)

    private fun updateSummaries() {
        val avgToday =
            if (todayHourly.isNotEmpty()) todayHourly.map { it.volumeLiters }.average().roundToInt()
            else 0

        val totalToday = todayHourly.sumOf { it.volumeLiters }

        val avgWeek =
            if (weekDaily.isNotEmpty()) weekDaily.map { it.volumeLiters }.average().roundToInt()
            else 0

        binding.tvAvgVolumeValue.text = avgToday.toString()
        binding.tvWasteValue.text = avgWeek.toString()
        binding.tvTotalLiters.text = totalToday.toString()
    }

    private fun highlightTab(selected: TabPeriod) {
        val blue = ContextCompat.getColor(requireContext(), R.color.primary_blue)
        val gray = ContextCompat.getColor(requireContext(), R.color.gray_dark)

        fun setTab(tv: TextView, isSelected: Boolean) {
            tv.setTextColor(if (isSelected) blue else gray)
            tv.textSize = if (isSelected) 14f else 13f
            tv.setTypeface(tv.typeface, if (isSelected) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        }

        setTab(binding.tabToday, selected == TabPeriod.TODAY)
        setTab(binding.tabThisWeek, selected == TabPeriod.THIS_WEEK)
        setTab(binding.tabThisMonth, selected == TabPeriod.THIS_MONTH)
    }

    //Dessin de l'histogramme selon l'onglet
    private fun drawCurrentTabChart() {
        when (currentTab) {
            TabPeriod.TODAY      -> drawTodayChart()
            TabPeriod.THIS_WEEK  -> drawWeekChart()
            TabPeriod.THIS_MONTH -> drawMonthChart()
        }
    }

    private fun drawTodayLineCurve() {
        val ctx = requireContext()
        val container = binding.containerLineChart
        container.removeAllViews()

        if (todayHourly.isEmpty()) return

        val density = ctx.resources.displayMetrics.density
        val lineColor = ContextCompat.getColor(ctx, R.color.primary_blue)

        val groups = listOf(
            "0-3H" to listOf(0, 1, 2, 3),
            "4-7H" to listOf(4, 5, 6, 7),
            "8-11H" to listOf(8, 9, 10, 11),
            "12-15H" to listOf(12, 13, 14, 15),
            "16-19H" to listOf(16, 17, 18, 19),
            "20-23H" to listOf(20, 21, 22, 23),
        )

        data class Point(val label: String, val value: Int)

        val points = groups.map { (label, hours) ->
            val sum = todayHourly
                .filter { parseHour(it.hourLabel) in hours }
                .sumOf { it.volumeLiters }
            Point(label, sum)
        }

        val maxValue = points.maxOf { it.value }.coerceAtLeast(1)

        val row = LinearLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM
        }

        points.forEach { p ->
            val ratio = p.value.toFloat() / maxValue.toFloat()
            val minHeightDp = 20f
            val maxHeightDp = 80f
            val hDp = minHeightDp + (maxHeightDp - minHeightDp) * ratio

            val pointColumn = LinearLayout(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                )
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }

            val segment = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (2 * density).toInt(),
                    (hDp * density).toInt()
                )
                setBackgroundColor(lineColor)
            }

            val dot = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (6 * density).toInt(),
                    (6 * density).toInt()
                ).apply {
                    topMargin = (2 * density).toInt()
                }
                background = ContextCompat.getDrawable(ctx, R.drawable.bg_line_point)
            }

            val columnContent = LinearLayout(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }
            columnContent.addView(segment)
            columnContent.addView(dot)

            pointColumn.addView(columnContent)
            row.addView(pointColumn)
        }

        container.addView(row)
    }

    // Histogramme pour "Aujourd'hui"
    private fun drawTodayChart() {
        val ctx = requireContext()
        val container = binding.barChartContainer
        container.removeAllViews()

        if (todayHourly.isEmpty()) return

        val groups = listOf(
            "0-3H" to listOf(0, 1, 2, 3),
            "4-7H" to listOf(4, 5, 6, 7),
            "8-11H" to listOf(8, 9, 10, 11),
            "12-15H" to listOf(12, 13, 14, 15),
            "16-19H" to listOf(16, 17, 18, 19),
            "20-23H" to listOf(20, 21, 22, 23),
        )

        data class GroupBar(val label: String, val value: Int)

        val grouped = groups.map { (label, hours) ->
            val sum = todayHourly
                .filter { entry -> parseHour(entry.hourLabel) in hours }
                .sumOf { it.volumeLiters }
            GroupBar(label, sum)
        }.filter { it.value > 0 || true }

        val maxValue = grouped.maxOf { it.value }.coerceAtLeast(1)
        val density = ctx.resources.displayMetrics.density
        val barColor = ContextCompat.getColor(ctx, R.color.primary_blue)
        val textColor = ContextCompat.getColor(ctx, R.color.gray_dark)

        // Ligne des barres
        val barsRow = LinearLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }

        // Ligne des labels
        val labelsRow = LinearLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
        }

        grouped.forEach { g ->
            val ratio = g.value.toFloat() / maxValue.toFloat()
            val minHeightDp = 20f
            val maxHeightDp = 100f
            val hDp = minHeightDp + (maxHeightDp - minHeightDp) * ratio

            // Barre
            val bar = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    (hDp * density).toInt(),
                    1f
                ).apply {
                    val m = (4 * density).toInt()
                    marginStart = m
                    marginEnd = m
                }
                setBackgroundColor(barColor)
            }

            // Label
            val tv = TextView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                gravity = Gravity.CENTER
                text = g.label
                setTextColor(textColor)
                textSize = 11f
            }

            barsRow.addView(bar)
            labelsRow.addView(tv)
        }

        container.addView(barsRow)
        container.addView(labelsRow)
        drawTodayLineCurve()

    }

    // Histogramme pour "Dans la semaine"
    private fun drawWeekChart() {
        val ctx = requireContext()
        val container = binding.barChartContainer
        container.removeAllViews()

        if (weekDaily.isEmpty()) return

        val density = ctx.resources.displayMetrics.density
        val barColor = ContextCompat.getColor(ctx, R.color.primary_blue)
        val textColor = ContextCompat.getColor(ctx, R.color.gray_dark)

        val maxValue = weekDaily.maxOf { it.volumeLiters }.coerceAtLeast(1)

        val barsRow = LinearLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }

        val labelsRow = LinearLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
        }

        weekDaily.forEach { day ->
            val ratio = day.volumeLiters.toFloat() / maxValue.toFloat()
            val minHeightDp = 20f
            val maxHeightDp = 120f
            val hDp = minHeightDp + (maxHeightDp - minHeightDp) * ratio

            val bar = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    (hDp * density).toInt(),
                    1f
                ).apply {
                    val m = (4 * density).toInt()
                    marginStart = m
                    marginEnd = m
                }
                setBackgroundColor(barColor)
            }

            val tv = TextView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                gravity = Gravity.CENTER
                text = day.dayLabel
                setTextColor(textColor)
                textSize = 11f
            }

            barsRow.addView(bar)
            labelsRow.addView(tv)
        }

        container.addView(barsRow)
        container.addView(labelsRow)
    }

    // Histogramme pour "Ce mois-ci"
    private fun drawMonthChart() {
        val ctx = requireContext()
        val container = binding.barChartContainer
        container.removeAllViews()

        if (monthDaily.isEmpty()) return

        val groups = monthDaily.chunked((monthDaily.size / 5).coerceAtLeast(1))

        data class GroupBar(val label: String, val value: Int)

        val grouped = groups.mapIndexed { index, list ->
            val sum = list.sumOf { it.volumeLiters }
            GroupBar("S${index + 1}", sum)
        }

        val density = ctx.resources.displayMetrics.density
        val barColor = ContextCompat.getColor(ctx, R.color.primary_blue)
        val textColor = ContextCompat.getColor(ctx, R.color.gray_dark)
        val maxValue = grouped.maxOf { it.value }.coerceAtLeast(1)

        val barsRow = LinearLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }

        val labelsRow = LinearLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
        }

        grouped.forEach { g ->
            val ratio = g.value.toFloat() / maxValue.toFloat()
            val minHeightDp = 20f
            val maxHeightDp = 120f
            val hDp = minHeightDp + (maxHeightDp - minHeightDp) * ratio

            val bar = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    (hDp * density).toInt(),
                    1f
                ).apply {
                    val m = (4 * density).toInt()
                    marginStart = m
                    marginEnd = m
                }
                setBackgroundColor(barColor)
            }

            val tv = TextView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                gravity = Gravity.CENTER
                text = g.label
                setTextColor(textColor)
                textSize = 11f
            }

            barsRow.addView(bar)
            labelsRow.addView(tv)
        }

        container.addView(barsRow)
        container.addView(labelsRow)
    }
    private fun parseHour(label: String): Int {
        // Ex: "0H", "12H" -> 0,12
        return label.trimEnd('H', 'h').toIntOrNull() ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}