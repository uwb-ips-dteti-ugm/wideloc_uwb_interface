package com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.FragmentTrackingStatisticsBinding
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.domain.model.Distance
import com.rizqi.wideloc.domain.model.Point
import com.rizqi.wideloc.domain.model.StatisticData
import com.rizqi.wideloc.domain.model.StatisticDatum
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.decorations.GridSpacingItemDecoration
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.TrackingStatisticsAdapter
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.tableview.Cell
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.tableview.ColumnHeader
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.tableview.RowHeader
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.tableview.TableViewAdapter
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.utils.toDisplayString

class TrackingStatisticsFragment :
    BaseFragment<FragmentTrackingStatisticsBinding>(FragmentTrackingStatisticsBinding::inflate) {

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    private lateinit var trackingStatisticsAdapter: TrackingStatisticsAdapter
    private lateinit var tableViewAdapter: TableViewAdapter
    private lateinit var devices: List<DeviceData>
    private lateinit var distances: List<Distance>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackingStatisticsAdapter = TrackingStatisticsAdapter() { item ->
            (parentFragment as? TrackingStatisticsBottomSheet)?.switchToFragment(
                StatisticDetailFragment(item.data.id)
            )
        }
        initDevicePositionsTable()

        trackingViewModel.session.observe(viewLifecycleOwner){session ->
            val lastPoints = session.deviceTrackingHistoryData.map {
                it.points.last()
            }
            val lastDistances = session.recordedDistances.last().distances
            val timestamp = session.deviceTrackingHistoryData.first().timestamp
            updateDevicePositionsTable(lastPoints, lastDistances, timestamp)
        }
        trackingViewModel.statisticsGroup.observe(viewLifecycleOwner){ statisticGroup ->
            trackingStatisticsAdapter.submitList(statisticGroup.getListOfAll())
        }

        binding.statisticsRecyclerViewFragmentTrackingStatistics.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = trackingStatisticsAdapter
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_8dp)
            addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, false))
            setHasFixedSize(true)
        }
        binding.sessionIdtextViewFragmentTrackingStatistics.text =
            getString(R.string.session_id, trackingViewModel.session.value?.sessionId.toString())
    }

    private fun recalculateContentHeight() {
        view?.post {
            (parentFragment as? TrackingStatisticsBottomSheet)?.recalculateHeight(
                listOf(
                    binding.root,
                ),
            )
        }
    }

    private fun initDevicePositionsTable() {
        devices = trackingViewModel.getSelectedDevicesAndCombination().first
        distances = trackingViewModel.getSelectedDevicesAndCombination().second

        val columnHeaderItems = devices.map { device ->
            ColumnHeader(device.name)
        }.toMutableList()

        distances.forEach { distance ->
            val device1 = devices.find { it.getCorrespondingPointId() == distance.point1.id }
            val device2 = devices.find { it.getCorrespondingPointId() == distance.point2.id }
            columnHeaderItems.add(
                ColumnHeader(
                    getString(R.string.distance_point1_point2, device1?.name, device2?.name)
                )
            )
        }

        tableViewAdapter = TableViewAdapter()
        binding.devicePositionTableViewFragmentTrackingStatistics.setAdapter(tableViewAdapter)
        tableViewAdapter.setAllItems(columnHeaderItems, listOf(), listOf())
    }

    private fun updateDevicePositionsTable(points: List<Point>, distances: List<Distance>, timestamp: Long? = null){
        val rowHeaderItem = RowHeader(timestamp.toString())
        val cellItems = mutableListOf<Cell>()
        devices.forEach { device ->
            val point = points.find { it.id == device.getCorrespondingPointId() }
            cellItems.add(
                Cell(
                    "(${point?.x?.value?.toDisplayString()}, ${point?.y?.value?.toDisplayString()})"
                )
            )
        }
        distances.forEach { distance ->
            cellItems.add(
                Cell(
                    distance.distance.toDisplayString()
                )
            )
        }

        tableViewAdapter.addRow(0, rowHeaderItem, cellItems)
    }
}