package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.AddDeviceBottomSheetBinding
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.ConnectViaWiFiFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddDeviceBottomSheet : BottomSheetDialogFragment() {

    private var _binding: AddDeviceBottomSheetBinding? = null
    val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddDeviceBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(binding.frameAddDeviceBottomSheet.id, ConnectViaWiFiFragment())
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
            val frame = binding.frameAddDeviceBottomSheet
            val dragHandle = binding.dragHandleCardViewAddDeviceBottomSheet.root

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
            Log.d("FragmentDebug", "recalculateHeight: ${contentHeight}")

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

            Log.d("BottomSheetHeight", "Content: $contentHeight, Drag: $dragHandleHeight, PaddingTop: $paddingTop, Bottom: $paddingBottom")

            bottomSheetLayout.layoutParams.height = minOf(totalHeight, maxHeight)
            bottomSheetLayout.requestLayout()

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        addDeviceViewModel.resetAll()
    }

}