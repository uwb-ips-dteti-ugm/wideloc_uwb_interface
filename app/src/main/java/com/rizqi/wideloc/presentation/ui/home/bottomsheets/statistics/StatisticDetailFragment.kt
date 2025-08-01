package com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics

import android.os.Bundle
import android.view.View
import com.rizqi.wideloc.databinding.FragmentStatisticDetailBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.formatter.ValueFormatter
import com.rizqi.wideloc.R
import com.rizqi.wideloc.domain.model.StatisticData
import com.rizqi.wideloc.presentation.ui.decorations.GridSpacingItemDecoration
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.StatisticHistoryAdapter
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.TrackingStatisticsAdapter
import com.rizqi.wideloc.utils.DateTimeFormat
import com.rizqi.wideloc.utils.formatTimestamp

class StatisticDetailFragment(
    private val statisticViewItem: TrackingStatisticsAdapter.StatisticViewItem
) : BaseFragment<FragmentStatisticDetailBinding>(FragmentStatisticDetailBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.historyRecyclerViewFragmentStatisticDetail.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = StatisticHistoryAdapter(
                items = statisticViewItem.data.data.map {
                    StatisticHistoryAdapter.StatisticDatumViewItem(
                        valueGrowth = StatisticHistoryAdapter.ValueGrowth.Up,
                        valueStatus = StatisticHistoryAdapter.ValueStatus.Bad,
                        unitResId = statisticViewItem.unitResId,
                        datum = it,
                    )
                }.toList()
            )
            setHasFixedSize(true)
        }
        binding.backLayoutFragmentStatisticDetail.setOnClickListener {
            childFragmentManager.popBackStack()
        }
        setupLineChart(statisticViewItem.data)

    }

    private fun setupLineChart(statisticData: StatisticData) {
        // Step 1: Sort data by timestamp to ensure correct order
        val sortedData = statisticData.data.sortedBy { it.timestamp }

        // Step 2: Convert to chart entries (X = index, Y = value)
        val entries = sortedData.mapIndexed { index, datum ->
            Entry(index.toFloat(), datum.value.toFloat())
        }

        // Step 3: Build dataset
        val dataSet = LineDataSet(entries, "${statisticData.name} (${statisticData.unit})").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 10f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        // Step 4: Format x-axis labels to show date/time from timestamp
        val xLabels = sortedData.map { formatTimestamp(it.timestamp, DateTimeFormat.hhmmss) }

        val xAxisFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return xLabels.getOrNull(index) ?: ""
            }
        }

        // Step 5: Configure chart
        binding.lineChartFragmentStatisticDetails.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            axisRight.isEnabled = false

            // X Axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = xAxisFormatter
                textColor = Color.DKGRAY
                axisLineColor = Color.GRAY
            }

            // Y Axis
            axisLeft.apply {
                textColor = Color.DKGRAY
                axisLineColor = Color.GRAY
                setDrawGridLines(true)
                axisMinimum = 0f
            }

            animateY(700)
            invalidate()
        }
    }

}