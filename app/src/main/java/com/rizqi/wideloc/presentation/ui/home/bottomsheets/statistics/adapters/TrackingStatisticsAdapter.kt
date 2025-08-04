package com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.wideloc.databinding.StatisticItemCardBinding
import com.rizqi.wideloc.domain.model.StatisticData

class TrackingStatisticsAdapter(
    private val items: List<StatisticViewItem>,
    private val onItemClick: (StatisticViewItem) -> Unit
) : RecyclerView.Adapter<TrackingStatisticsAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: StatisticItemCardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: StatisticViewItem){
            binding.iconImageViewStatisticItemCard.setImageResource(item.iconResId)
            item.nameResId?.let {
                binding.nameTextViewStatisticItemCard.setText(it)
            } ?: binding.nameTextViewStatisticItemCard.setText(item.name)
            binding.unitTextViewStatisticItemCard.setText(item.unitResId)

            val lastDatum = item.data.data.last().value
            binding.valueTextViewStatisticItemCard.text = lastDatum.toString()

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StatisticItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    data class StatisticViewItem(
        @StringRes
        val nameResId: Int?,
        val name: String = "",
        @DrawableRes
        val iconResId: Int,
        @StringRes
        val unitResId: Int,
        val data: StatisticData,
    )
}