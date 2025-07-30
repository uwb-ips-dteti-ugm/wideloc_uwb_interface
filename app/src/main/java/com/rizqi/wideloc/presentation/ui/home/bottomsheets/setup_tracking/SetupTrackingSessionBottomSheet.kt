package com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.SetupTrackingSessionBottomSheetBinding
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.receiver.WifiInfoReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetupTrackingSessionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: SetupTrackingSessionBottomSheetBinding? = null
    val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var wifiInfoReceiver: WifiInfoReceiver

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SetupTrackingSessionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wifiInfoReceiver = WifiInfoReceiver(requireContext(), this)

        childFragmentManager.beginTransaction()
            .replace(binding.frameSetupTrackingSessionBottomSheet.id, SetupTrackingSessionFragment())
            .commit()

        wifiInfoReceiver.startListening { info ->
            lifecycleScope.launch(Dispatchers.Main) {
                trackingViewModel.setConnectedWifi(info?.second)
            }
        }

        trackingViewModel.connectedWifiInfo.observe(viewLifecycleOwner){wifiInfo ->
            binding.connectedNetworkTextViewSetupTrackingSessionBottomSheet.text =
                getString(
                    R.string.network_connected_to,
                    if (wifiInfo != null) wifiInfo.ssid
                    else getString(R.string.none)
                )
        }
        trackingViewModel.connectedWifiInfoError.observe(viewLifecycleOwner){wifiError ->
            binding.networkNotSameTextViewSetupTrackingSessionBottomSheet.apply {
                text = wifiError
                visibility = if (wifiError == null) View.GONE else View.VISIBLE
            }

        }

    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onDestroyView() {
        super.onDestroyView()
        wifiInfoReceiver.stopListening()
        _binding = null
    }

    fun toggleWifiInfoVisibility(isVisible: Boolean) {
        binding.wifiInfoLayoutSetupTrackingSessionBottomSheet.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun recalculateHeight(contents: List<View?>) {
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val bottomSheetLayout = bottomSheet as? ViewGroup ?: return
        bottomSheetLayout.invalidate()

        bottomSheetLayout.post {
            val maxHeight = (resources.displayMetrics.heightPixels * 0.95).toInt()

            val rootLayout = binding.root
            val frame = binding.frameSetupTrackingSessionBottomSheet
            val dragHandle = binding.dragHandleCardViewSetupTrackingSessionBottomSheet.root
            val wifiInfo = binding.wifiInfoLayoutSetupTrackingSessionBottomSheet

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

            // 3. Measure wifiInfo view
            val wifiInfoHeight = if (wifiInfo.height > 0) {
                wifiInfo.height
            } else {
                // Fallback measure if not yet laid out
                wifiInfo.measure(
                    View.MeasureSpec.makeMeasureSpec(rootLayout.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
                )
                wifiInfo.measuredHeight
            }

            // 4. Calculate padding
            val paddingTop = rootLayout.paddingTop
            val paddingBottom = rootLayout.paddingBottom

            // 5. Total height
            val totalHeight = contentHeight + dragHandleHeight + paddingTop + paddingBottom + frame.marginTop + wifiInfoHeight

            bottomSheetLayout.layoutParams.height = minOf(totalHeight, maxHeight)
            bottomSheetLayout.requestLayout()

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

}