package com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.wideloc.databinding.StatisticItemCardBinding
import com.rizqi.wideloc.domain.model.StatisticData
import com.rizqi.wideloc.utils.toDisplayString

class TrackingStatisticsAdapter(
    private val onItemClick: (StatisticViewItem) -> Unit
) : RecyclerView.Adapter<TrackingStatisticsAdapter.ViewHolder>(){

    private val items: MutableList<StatisticViewItem> = mutableListOf()

    inner class ViewHolder(val binding: StatisticItemCardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: StatisticViewItem){
            binding.iconImageViewStatisticItemCard.setImageResource(item.iconResId)
            item.nameResId?.let {
                binding.nameTextViewStatisticItemCard.setText(it)
            } ?: binding.nameTextViewStatisticItemCard.setText(item.name)
            binding.unitTextViewStatisticItemCard.setText(item.unitResId)

            val lastDatum = item.data.data.lastOrNull()?.value
            binding.valueTextViewStatisticItemCard.text = lastDatum?.toDisplayString(4) ?: "-"

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

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<StatisticViewItem>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
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