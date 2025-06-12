package com.rizqi.wideloc.presentation.ui.devices.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.DeviceCardBinding
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.utils.formatToString
import java.io.File

class DevicesAdapter : RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder>() {

    private val devices = mutableListOf<DeviceData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = DeviceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount(): Int = devices.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newDevices: List<DeviceData>){
        devices.clear()
        devices.addAll(newDevices)
        notifyDataSetChanged()
    }

    inner class DeviceViewHolder(private val binding: DeviceCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: DeviceData) {
            Glide.with(binding.deviceImageViewDeviceCard.context)
                .load(File(device.imageUrl))
                .into(binding.deviceImageViewDeviceCard)
            binding.deviceNameTextViewDeviceCard.text = device.name
            binding.connectedTimeTextViewDeviceCard.text = device.lastConnectedAt.formatToString()
        }
    }
}