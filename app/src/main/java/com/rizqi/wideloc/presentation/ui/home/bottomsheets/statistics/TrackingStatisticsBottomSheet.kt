package com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.TrackingStatisticsBottomSheetBinding
import com.rizqi.wideloc.domain.model.StatisticData
import com.rizqi.wideloc.domain.model.StatisticDatum
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.TrackingStatisticsAdapter
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingStatisticsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: TrackingStatisticsBottomSheetBinding? = null
    val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TrackingStatisticsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val layoutParams = it.layoutParams
            layoutParams.height = (resources.displayMetrics.heightPixels * 0.95).toInt()
            it.layoutParams = layoutParams

            BottomSheetBehavior.from(it).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(binding.frameTrackingStatisticsBottomSheet.id, TrackingStatisticsFragment())
            .commit()
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun recalculateHeight(contents: List<View?>) {
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val bottomSheetLayout = bottomSheet as? ViewGroup ?: return
        bottomSheetLayout.invalidate()

        bottomSheetLayout.post {
            val maxHeight = (resources.displayMetrics.heightPixels * 0.95).toInt()

            val rootLayout = binding.root
            val frame = binding.frameTrackingStatisticsBottomSheet
            val dragHandle = binding.dragHandleCardViewTrackingStatisticsBottomSheet.root

            // 1. Measure contents inside the FrameLayout (fragment content)
            var contentHeight = 0
            contents.forEach { content ->
                content?.measure(
                    View.MeasureSpec.makeMeasureSpec(frame.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
                )
                content?.let {
                    contentHeight += it.measuredHeight + it.marginTop + it.marginBottom
                }
            }

            // 2. Measure drag handle view
            val dragHandleHeight = if (dragHandle.height > 0) {
                dragHandle.height
            } else {
                // Fallback measure if not yet laid out
                dragHandle.measure(
                    View.MeasureSpec.makeMeasureSpec(rootLayout.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
                )
                dragHandle.measuredHeight
            }

            // 3. Calculate padding
            val paddingTop = rootLayout.paddingTop
            val paddingBottom = rootLayout.paddingBottom

            // 4. Total height
            val totalHeight = contentHeight + dragHandleHeight + paddingTop + paddingBottom + frame.marginTop

            bottomSheetLayout.layoutParams.height = minOf(totalHeight, maxHeight)
            bottomSheetLayout.requestLayout()

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    fun switchToFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(binding.frameTrackingStatisticsBottomSheet.id, fragment)
            .addToBackStack(null)
            .commit()
    }

}