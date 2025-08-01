package com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.FragmentTrackingStatisticsBinding
import com.rizqi.wideloc.domain.model.StatisticData
import com.rizqi.wideloc.domain.model.StatisticDatum
import com.rizqi.wideloc.domain.model.StatisticViewItem
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.decorations.GridSpacingItemDecoration
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.TrackingStatisticsAdapter

class TrackingStatisticsFragment :
    BaseFragment<FragmentTrackingStatisticsBinding>(FragmentTrackingStatisticsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            StatisticViewItem(
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
            StatisticViewItem(
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
            StatisticViewItem(
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
            adapter = TrackingStatisticsAdapter(items)
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_8dp)
            addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
            setHasFixedSize(true)
        }

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