package com.rizqi.wideloc.presentation.ui.connect_via_bluetooth.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.AvailableDeviceToPairCardBinding
import com.rizqi.wideloc.databinding.DeviceCardBinding

data class AvailableDeviceItem(
    val name: String,
    val macAddress: String
)

class AvailableDevicesAdapter : RecyclerView.Adapter<AvailableDevicesAdapter.AvailableDeviceViewHolder>() {

    private val dummyDevices = listOf(
        AvailableDeviceItem("Client Tag 1", "12:34:56:78:90"),
        AvailableDeviceItem("Client Tag 2", "12:34:56:78:90"),
        AvailableDeviceItem("Client Tag 3", "12:34:56:78:90")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableDeviceViewHolder {
        val binding = AvailableDeviceToPairCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvailableDeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvailableDeviceViewHolder, position: Int) {
        holder.bind(dummyDevices[position])
    }

    override fun getItemCount(): Int = dummyDevices.size

    inner class AvailableDeviceViewHolder(private val binding: AvailableDeviceToPairCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: AvailableDeviceItem) {
            binding.nameTextViewAvailableDeviceToPairCard.text = device.name
            binding.macAddressTextViewAvailableDeviceToPairCard.text = device.macAddress
        }
    }
}