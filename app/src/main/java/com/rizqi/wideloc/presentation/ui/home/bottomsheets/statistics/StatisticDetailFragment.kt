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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.formatter.ValueFormatter
import com.rizqi.wideloc.R
import com.rizqi.wideloc.domain.model.StatisticData
import com.rizqi.wideloc.domain.model.StatisticDatum
import com.rizqi.wideloc.presentation.ui.decorations.GridSpacingItemDecoration
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.StatisticHistoryAdapter
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.TrackingStatisticsAdapter
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.utils.DateTimeFormat
import com.rizqi.wideloc.utils.formatTimestamp

class StatisticDetailFragment(
    private val statisticDataId: String,
    private val statisticName: String,
) : BaseFragment<FragmentStatisticDetailBinding>(FragmentStatisticDetailBinding::inflate) {

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    private lateinit var statisticHistoryAdapter : StatisticHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statisticHistoryAdapter = StatisticHistoryAdapter()

        trackingViewModel.statisticsGroup.observe(viewLifecycleOwner){statisticGroup ->
            val statisticViewItem = statisticGroup.getListOfAll().find { it.data.id == statisticDataId } ?: return@observe
            val statisticData = statisticViewItem.data
            setupLineChart(statisticData)
            statisticHistoryAdapter.submitList(
                statisticData.data.mapIndexed { index, statisticDatum ->
                    val prevDatum = if (index == 0) statisticDatum else statisticData.data[index - 1]
                    StatisticHistoryAdapter.StatisticDatumViewItem(
                        valueGrowth = getValueGrowth(prevDatum, statisticDatum),
                        valueStatus = StatisticHistoryAdapter.ValueStatus.Good,
                        unitResId = statisticViewItem.unitResId,
                        datum = statisticDatum
                    )
                }
            )
        }

        binding.statisticTitleTextViewFragmentStatisticDetails.text = statisticName
        binding.historyRecyclerViewFragmentStatisticDetail.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = statisticHistoryAdapter
        }
        binding.backLayoutFragmentStatisticDetail.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

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

            // Interaction
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            axisRight.isEnabled = false

            // Enable horizontal scrolling
            setVisibleXRangeMaximum(10f) // Show only last 10 items
            moveViewToX(entries.size.toFloat()) // Scroll to end

            // X Axis formatting
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

    private fun getValueGrowth(prevDatum: StatisticDatum, currentDatum: StatisticDatum): StatisticHistoryAdapter.ValueGrowth {
        if (prevDatum.value <= currentDatum.value) return StatisticHistoryAdapter.ValueGrowth.Up
        else return StatisticHistoryAdapter.ValueGrowth.Down
    }

}