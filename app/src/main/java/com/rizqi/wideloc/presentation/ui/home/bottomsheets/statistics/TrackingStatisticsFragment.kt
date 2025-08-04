package com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.FragmentTrackingStatisticsBinding
import com.rizqi.wideloc.domain.model.StatisticData
import com.rizqi.wideloc.domain.model.StatisticDatum
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.decorations.GridSpacingItemDecoration
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.TrackingStatisticsAdapter
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.tableview.Cell
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.tableview.ColumnHeader
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.tableview.RowHeader
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.tableview.TableViewAdapter

class TrackingStatisticsFragment :
    BaseFragment<FragmentTrackingStatisticsBinding>(FragmentTrackingStatisticsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            TrackingStatisticsAdapter.StatisticViewItem(
                nameResId = R.string.latency,
                iconResId = R.drawable.ic_bell,
                unitResId = R.string.milliseconds,
                data = StatisticData(
                    id = "",
                    name = "Latency",
                    unit = "milliseconds",
                    data = listOf(
                        StatisticDatum(
                            timestamp = System.currentTimeMillis(),
                            value = 230.0
                        ),
                    )
                )
            ),
            TrackingStatisticsAdapter.StatisticViewItem(
                nameResId = R.string.accuracy,
                iconResId = R.drawable.ic_plus,
                unitResId = R.string.cm,
                data = StatisticData(
                    id = "",
                    name = "Accuracy",
                    unit = "cm",
                    data = listOf(
                        StatisticDatum(
                            timestamp = System.currentTimeMillis(),
                            value = 10.0
                        ),
                    )
                )
            ),
            TrackingStatisticsAdapter.StatisticViewItem(
                nameResId = R.string.power_consumption,
                iconResId = R.drawable.ic_chart,
                unitResId = R.string.mwatt,
                data = StatisticData(
                    id = "",
                    name = "Power Consumption",
                    unit = "mW",
                    data = listOf(
                        StatisticDatum(
                            timestamp = System.currentTimeMillis(),
                            value = 50.0
                        ),
                    )
                )
            ),
        )

        binding.statisticsRecyclerViewFragmentTrackingStatistics.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = TrackingStatisticsAdapter(items){ item ->
                (parentFragment as? TrackingStatisticsBottomSheet)?.switchToFragment(
                    StatisticDetailFragment(item)
                )
            }
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_8dp)
            addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, false))
            setHasFixedSize(true)
        }

        val mRowHeaderList = listOf(
            RowHeader("A"),
            RowHeader("B"),
            RowHeader("C"),
            RowHeader("D"),
        )
        val mColumnHeaderList = listOf(
            ColumnHeader("1"),
            ColumnHeader("2"),
            ColumnHeader("3"),
            ColumnHeader("4"),
        )
        val mCellList = listOf(
            listOf(
                Cell("A1"),
                Cell("A2"),
                Cell("A3"),
                Cell("A4"),
            ),
            listOf(
                Cell("B1"),
                Cell("B2"),
                Cell("B3"),
                Cell("B4"),
            ),
            listOf(
                Cell("C1"),
                Cell("C2"),
                Cell("C3"),
                Cell("C4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),
            listOf(
                Cell("D1"),
                Cell("D2"),
                Cell("D3"),
                Cell("D4"),
            ),

        )
        val tableAdapter = TableViewAdapter()
        binding.devicePositionTableViewFragmentTrackingStatistics.setAdapter(tableAdapter)
        tableAdapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList)

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
}