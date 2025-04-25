package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.AddDeviceBottomSheetBinding
import com.rizqi.wideloc.databinding.FragmentSelectProtocolBinding

class AddDeviceBottomSheet : BottomSheetDialogFragment() {

    private var _binding: AddDeviceBottomSheetBinding? = null
    val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

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
            .replace(binding.frameAddDeviceBottomSheet.id, SelectProtocolFragment())
            .commit()

    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun recalculateHeight(content: View?) {
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val bottomSheetLayout = bottomSheet as? ViewGroup ?: return
        bottomSheetLayout.invalidate()

        bottomSheetLayout.post {
            val maxHeight = (resources.displayMetrics.heightPixels * 0.97).toInt()

            val rootLayout = binding.root
            val frame = binding.frameAddDeviceBottomSheet
            val dragHandle = binding.dragHandleCardViewAddDeviceBottomSheet.root

            // 1. Measure content inside the FrameLayout (fragment content)
            content?.measure(
                View.MeasureSpec.makeMeasureSpec(frame.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.UNSPECIFIED
            )
            val contentHeight = content?.measuredHeight ?: 0
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

}