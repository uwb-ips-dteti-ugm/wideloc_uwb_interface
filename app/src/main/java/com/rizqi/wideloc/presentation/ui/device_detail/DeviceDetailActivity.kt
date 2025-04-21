package com.rizqi.wideloc.presentation.ui.device_detail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.ActivityDeviceDetailBinding
import com.rizqi.wideloc.databinding.ActivityMainBinding
import com.rizqi.wideloc.presentation.ui.decorations.GridSpacingItemDecoration
import com.rizqi.wideloc.presentation.ui.device_detail.adapters.DeviceImageAdapter
import com.rizqi.wideloc.presentation.ui.device_detail.adapters.DeviceParameter
import com.rizqi.wideloc.presentation.ui.device_detail.adapters.DeviceParameterAdapter

class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDeviceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.deviceDetailBottomSheet.root)

        setupBottomSheetCallback()
        setupRecyclerViews()
    }

    private fun setupBottomSheetCallback() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val stateText = when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> "STATE_COLLAPSED"
                    BottomSheetBehavior.STATE_EXPANDED -> "STATE_EXPANDED"
                    BottomSheetBehavior.STATE_DRAGGING -> "STATE_DRAGGING"
                    BottomSheetBehavior.STATE_SETTLING -> "STATE_SETTLING"
                    BottomSheetBehavior.STATE_HIDDEN -> "STATE_HIDDEN"
                    else -> "OTHER_STATE"
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun setupRecyclerViews() {
        // Dummy images
        val dummyImages = listOf(
            R.drawable.map_dummy,
            R.drawable.map_dummy,
            R.drawable.map_dummy,
            R.drawable.map_dummy,
            R.drawable.map_dummy,
        )
        val imageAdapter = DeviceImageAdapter(dummyImages)
        binding.deviceDetailBottomSheet.deviceImagesRecyclerViewDeviceDetailBottomSheet.apply {
            layoutManager = LinearLayoutManager(
                this@DeviceDetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = imageAdapter
        }

        // Dummy parameters
        val dummyParams = listOf(
            DeviceParameter(R.drawable.ic_battery_50, "Battery", "70", "%"),
            DeviceParameter(R.drawable.ic_battery_50, "Signal", "-60", "dBm"),
            DeviceParameter(R.drawable.ic_battery_50, "Temp", "38", "°C"),
            DeviceParameter(R.drawable.ic_battery_50, "Latency", "25", "ms"),
            DeviceParameter(R.drawable.ic_battery_50, "Distance", "3.2", "m"),
            DeviceParameter(R.drawable.ic_battery_50, "RSSI", "-85", "dBm")
        )

        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_16dp)
        val parameterAdapter = DeviceParameterAdapter(dummyParams)
        binding.deviceDetailBottomSheet.deviceParameterRecylerViewDeviceDetailBottomSheet.apply {
            layoutManager = GridLayoutManager(this@DeviceDetailActivity, 3)
            addItemDecoration(GridSpacingItemDecoration(3, spacing))
            adapter = parameterAdapter
        }
    }
}