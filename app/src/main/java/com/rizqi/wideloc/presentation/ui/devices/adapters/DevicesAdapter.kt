package com.rizqi.wideloc.presentation.ui.devices.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.DeviceCardBinding

data class DeviceItem(
    val imageResId: Int,
    val name: String,
    val connectedTime: String
)

class DevicesAdapter : RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder>() {

    private val dummyDevices = listOf(
        DeviceItem(R.drawable.map_dummy, "Client Tag 1", "Connected at Tue, 4 March 2025 07.00"),
        DeviceItem(R.drawable.map_dummy, "Client Tag 2", "Connected at Wed, 5 March 2025 08.30"),
        DeviceItem(R.drawable.map_dummy, "Client Tag 3", "Connected at Thu, 6 March 2025 09.15")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = DeviceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(dummyDevices[position])
    }

    override fun getItemCount(): Int = dummyDevices.size

    inner class DeviceViewHolder(private val binding: DeviceCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: DeviceItem) {
            binding.deviceImageViewDeviceCard.setImageResource(device.imageResId)
            binding.deviceNameTextViewDeviceCard.text = device.name
            binding.connectedTimeTextViewDeviceCard.text = device.connectedTime
        }
    }
}