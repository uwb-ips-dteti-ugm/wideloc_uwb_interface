package com.rizqi.wideloc.presentation.ui.device_detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.wideloc.databinding.DeviceImageItemBinding

class DeviceImageAdapter(private val imageList: List<Int>) :
    RecyclerView.Adapter<DeviceImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: DeviceImageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = DeviceImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.binding.deviceImageImageViewDeviceImageItem.setImageResource(imageList[position])
    }
}