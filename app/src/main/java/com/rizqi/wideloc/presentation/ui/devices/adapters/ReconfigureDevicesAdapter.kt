package com.rizqi.wideloc.presentation.ui.devices.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.ReconfigureDeviceCardBinding

class ReconfigureDevicesAdapter : RecyclerView.Adapter<ReconfigureDevicesAdapter.DeviceViewHolder>() {

    private val dummyDevices = listOf(
        DeviceItem(R.drawable.map_dummy, "Anchor Tag A", "Connected at Mon, 3 March 2025 10.00"),
        DeviceItem(R.drawable.map_dummy, "Anchor Tag B", "Connected at Tue, 4 March 2025 11.45"),
        DeviceItem(R.drawable.map_dummy, "Anchor Tag C", "Connected at Wed, 5 March 2025 12.30"),
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ReconfigureDeviceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(dummyDevices[position])
    }

    override fun getItemCount(): Int = dummyDevices.size

    inner class DeviceViewHolder(private val binding: ReconfigureDeviceCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: DeviceItem) {
            binding.deviceImageViewDeviceCard.setImageResource(device.imageResId)
            binding.deviceNameTextViewDeviceCard.text = device.name
            binding.connectedTimeTextViewDeviceCard.text = device.connectedTime
        }
    }
}