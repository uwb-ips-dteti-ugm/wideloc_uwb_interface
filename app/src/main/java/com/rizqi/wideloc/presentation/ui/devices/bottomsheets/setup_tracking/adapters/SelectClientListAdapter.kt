package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.ReconfigureDeviceCardBinding
import com.rizqi.wideloc.databinding.SetupTrackingSessionDeviceCardBinding
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.utils.DomainDataMapper.asWifiProtocolEntity
import com.rizqi.wideloc.utils.formatToString
import java.io.File

class SelectClientListAdapter(
    private val onClick: (DeviceData) -> Unit
) : RecyclerView.Adapter<SelectClientListAdapter.DeviceViewHolder>() {

    private val devices = mutableListOf<DeviceData>()
    private val selectedDevices = mutableListOf<DeviceData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = SetupTrackingSessionDeviceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun getSelectedDevices() = selectedDevices

    @SuppressLint("NotifyDataSetChanged")
    fun selectAll() {
        selectedDevices.clear()
        selectedDevices.addAll(devices)
        notifyDataSetChanged()
    }

    inner class DeviceViewHolder(private val binding: SetupTrackingSessionDeviceCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: DeviceData) {
            val context = binding.root.context

            val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = 32
            binding.root.layoutParams = layoutParams

            if (selectedDevices.contains(device)) {
                binding.root.background = ContextCompat.getDrawable(context, R.drawable.selected_device_card_background)
                binding.deviceNameTextViewSetupTrackingSessionDeviceCard.setTextColor(ContextCompat.getColor(context, R.color.text_on_primary))
                val onSecondaryColor = ContextCompat.getColor(context, R.color.text_on_primary_secondary)
                binding.staSSIDImageViewSetupTrackingSessionDeviceCard.setColorFilter(onSecondaryColor)
                binding.staSSIDTextViewSetupTrackingSessionDeviceCard.setTextColor(onSecondaryColor)
                binding.dnsImageViewSetupTrackingSessionDeviceCard.setColorFilter(onSecondaryColor)
                binding.dnsTextViewSetupTrackingSessionDeviceCard.setTextColor(onSecondaryColor)
                binding.selectedTextViewSetupTrackingSessionDeviceCard.visibility = View.VISIBLE
            } else {
                binding.root.background = ContextCompat.getDrawable(context, R.drawable.device_card_background)
                binding.deviceNameTextViewSetupTrackingSessionDeviceCard.setTextColor(ContextCompat.getColor(context, R.color.text_default))
                val onSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary)
                binding.staSSIDImageViewSetupTrackingSessionDeviceCard.setColorFilter(onSecondaryColor)
                binding.staSSIDTextViewSetupTrackingSessionDeviceCard.setTextColor(onSecondaryColor)
                binding.dnsImageViewSetupTrackingSessionDeviceCard.setColorFilter(onSecondaryColor)
                binding.dnsTextViewSetupTrackingSessionDeviceCard.setTextColor(onSecondaryColor)
                binding.selectedTextViewSetupTrackingSessionDeviceCard.visibility = View.GONE
            }

            Glide.with(context)
                .load(File(device.imageUrl))
                .placeholder(R.drawable.esp32_uwb_dw3000)
                .error(R.drawable.esp32_uwb_dw3000)
                .into(binding.imageDeviceImageViewSetupTrackingSessionDeviceCard)
            binding.deviceNameTextViewSetupTrackingSessionDeviceCard.text = device.name
            binding.staSSIDTextViewSetupTrackingSessionDeviceCard.text =
                device.protocol.asWifiProtocolEntity()?.networkSSID ?: context.getString(R.string.network_not_configured_yet)
            binding.dnsTextViewSetupTrackingSessionDeviceCard.text =
                device.protocol.asWifiProtocolEntity()?.mdns ?: context.getString(R.string.dns_not_configured_yet)

            binding.root.setOnClickListener {
                if (selectedDevices.contains(device)){
                    selectedDevices.remove(device)
                } else {
                    selectedDevices.add(device)
                }
                notifyDataSetChanged()
                onClick(device)
            }
        }
    }
}