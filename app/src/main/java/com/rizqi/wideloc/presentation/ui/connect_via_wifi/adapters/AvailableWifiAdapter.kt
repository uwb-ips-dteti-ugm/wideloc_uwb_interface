package com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.ItemWifiBinding

data class WifiInformation(
    val ssid: String,
    val ipv4: String,
    val password: String = "",
)

class AvailableWifiAdapter(
    private var selectedWifiInformation: WifiInformation? = null,
    private val onItemClick: (WifiInformation) -> Unit,
) : ListAdapter<WifiInformation, AvailableWifiAdapter.AvailableWifiViewHolder>(DIFF_CALLBACK){

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WifiInformation>() {
            override fun areItemsTheSame(
                oldItem: WifiInformation,
                newItem: WifiInformation
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: WifiInformation,
                newItem: WifiInformation
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class AvailableWifiViewHolder(private val binding: ItemWifiBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("NotifyDataSetChanged")
        fun bind(wifiInformation: WifiInformation){
            binding.wifiSSIDTextViewItemWifi.text = wifiInformation.ssid
            binding.wifiIPTextViewItemWifi.text = wifiInformation.ipv4
            binding.root.setOnClickListener {
                onItemClick(wifiInformation)
                selectedWifiInformation = wifiInformation
                notifyDataSetChanged()
            }

            val context = binding.root.context
            if (selectedWifiInformation?.ssid == wifiInformation.ssid && selectedWifiInformation?.ipv4 == wifiInformation.ipv4) {
                binding.root.background = ContextCompat.getDrawable(context, R.drawable.selected_device_card_background)
                binding.wifiSSIDTextViewItemWifi.setTextColor(ContextCompat.getColor(context, R.color.text_on_primary))
                binding.wifiIPTextViewItemWifi.setTextColor(ContextCompat.getColor(context, R.color.text_on_primary_secondary))
            } else {
                binding.root.background = ContextCompat.getDrawable(context, R.drawable.device_card_background)
                binding.wifiSSIDTextViewItemWifi.setTextColor(ContextCompat.getColor(context, R.color.text_default))
                binding.wifiIPTextViewItemWifi.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableWifiViewHolder {
        val binding = ItemWifiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvailableWifiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvailableWifiViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}