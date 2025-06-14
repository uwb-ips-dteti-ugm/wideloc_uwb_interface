package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device

import android.content.DialogInterface
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.AddDeviceBottomSheetBinding
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.ConnectViaWiFiFragment
import com.rizqi.wideloc.receiver.WifiInfoReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AddDeviceBottomSheet : BottomSheetDialogFragment() {

    private var _binding: AddDeviceBottomSheetBinding? = null
    val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    private lateinit var wifiInfoReceiver: WifiInfoReceiver

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

        wifiInfoReceiver = WifiInfoReceiver(requireContext(), this)

        childFragmentManager.beginTransaction()
            .replace(binding.frameAddDeviceBottomSheet.id, ConnectViaWiFiFragment())
            .commit()

        wifiInfoReceiver.startListening { info ->
            lifecycleScope.launch(Dispatchers.Main) {
                addDeviceViewModel.setConnectedWifi(info?.second)
            }
        }

        addDeviceViewModel.connectedWifiInfo.observe(viewLifecycleOwner){wifiInfo ->
            binding.connectedNetworkTextViewAddDeviceBottomSheet.text =
                getString(
                    R.string.network_connected_to,
                    if (wifiInfo != null) wifiInfo.ssid
                    else getString(R.string.none)
                )
        }
        addDeviceViewModel.connectedWifiInfoError.observe(viewLifecycleOwner){wifiError ->
            binding.networkNotSameTextViewAddDeviceBottomSheet.apply {
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
        binding.wifiInfoLayoutAddDeviceBottomSheet.visibility = if (isVisible) View.VISIBLE else View.GONE
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        addDeviceViewModel.resetAll()
    }

}