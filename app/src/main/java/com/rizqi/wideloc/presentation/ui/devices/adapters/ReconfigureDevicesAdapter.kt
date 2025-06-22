package com.rizqi.wideloc.presentation.ui.devices.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.ReconfigureDeviceCardBinding
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.utils.DomainDataMapper.asWifiProtocolEntity
import com.rizqi.wideloc.utils.formatToString
import java.io.File

class ReconfigureDevicesAdapter : RecyclerView.Adapter<ReconfigureDevicesAdapter.DeviceViewHolder>() {

    private val devices = mutableListOf<DeviceData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ReconfigureDeviceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class DeviceViewHolder(private val binding: ReconfigureDeviceCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: DeviceData) {
            val context = binding.root.context

            Glide.with(binding.deviceImageViewDeviceCard.context)
                .load(File(device.imageUrl))
                .into(binding.deviceImageViewDeviceCard)
            binding.deviceNameTextViewDeviceCard.text = device.name
            binding.connectedTimeTextViewDeviceCard.text =
                if (device.lastConnectedAt == null) context.getString(R.string.never_connected_yet)
                else device.lastConnectedAt.formatToString()
            binding.staSSIDTextViewDeviceCard.text = device.protocol.asWifiProtocolEntity()?.networkSSID ?: context.getString(R.string.network_not_configured_yet)
            binding.dnsTextViewDeviceCard.text = device.protocol.asWifiProtocolEntity()?.mdns ?: context.getString(R.string.network_not_configured_yet)
            binding.roleTextViewDeviceCard.text = device.role.name
        }
    }
}