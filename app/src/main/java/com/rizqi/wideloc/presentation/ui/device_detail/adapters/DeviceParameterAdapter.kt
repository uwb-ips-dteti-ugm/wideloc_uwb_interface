package com.rizqi.wideloc.presentation.ui.device_detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.wideloc.databinding.DeviceParameterCardBinding

data class DeviceParameter(
    val iconResId: Int,
    val title: String,
    val value: String,
    val unit: String
)

class DeviceParameterAdapter(private val parameters: List<DeviceParameter>) :
    RecyclerView.Adapter<DeviceParameterAdapter.ParameterViewHolder>() {

    inner class ParameterViewHolder(val binding: DeviceParameterCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParameterViewHolder {
        val binding = DeviceParameterCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParameterViewHolder(binding)
    }

    override fun getItemCount(): Int = parameters.size

    override fun onBindViewHolder(holder: ParameterViewHolder, position: Int) {
        val item = parameters[position]
        holder.binding.apply {
            titleTextViewParameterCard.getChildAt(0)?.let { (it as? android.widget.ImageView)?.setImageResource(item.iconResId) }
            titleTextViewParameterCard.getChildAt(1)?.let { (it as? android.widget.TextView)?.text = item.title }
            valueTextViewDeviceParameterCard.text = item.value
            unitTextViewDeviceParameterCard.text = item.unit
        }
    }
}