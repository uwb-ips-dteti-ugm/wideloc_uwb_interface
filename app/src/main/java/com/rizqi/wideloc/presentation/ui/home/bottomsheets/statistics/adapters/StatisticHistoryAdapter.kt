package com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.wideloc.databinding.StatisticDatumItemBinding
import com.rizqi.wideloc.domain.model.StatisticDatum
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.StatisticHistoryAdapter.ValueGrowth.Down
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.StatisticHistoryAdapter.ValueGrowth.Up
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.StatisticHistoryAdapter.ValueStatus.*
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.adapters.TrackingStatisticsAdapter.StatisticViewItem
import com.rizqi.wideloc.utils.DateTimeFormat
import com.rizqi.wideloc.utils.formatTimestamp
import com.rizqi.wideloc.utils.toDisplayString

class StatisticHistoryAdapter(
) : RecyclerView.Adapter<StatisticHistoryAdapter.ViewHolder>(){

    private val items: MutableList<StatisticDatumViewItem> = mutableListOf()

    inner class ViewHolder(val binding: StatisticDatumItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: StatisticDatumViewItem){
            when (item.valueGrowth){
                Up -> {
                    binding.upCardViewStatisticDatumItemCard.visibility = View.VISIBLE
                    binding.downCardViewStatisticDatumItemCard.visibility = View.GONE
                }
                Down -> {
                    binding.upCardViewStatisticDatumItemCard.visibility = View.GONE
                    binding.downCardViewStatisticDatumItemCard.visibility = View.VISIBLE
                }
            }

//            when (item.valueStatus){
//                Good -> {
//                    binding.goodCardViewStatisticDatumItemCard.visibility = View.VISIBLE
//                    binding.badCardViewStatisticDatumItemCard.visibility = View.GONE
//                }
//                Bad -> {
//                    binding.goodCardViewStatisticDatumItemCard.visibility = View.GONE
//                    binding.badCardViewStatisticDatumItemCard.visibility = View.VISIBLE
//                }
//            }

            val datum = item.datum
            binding.dateTextViewStatisticDatumItemCard.text = formatTimestamp(datum.timestamp)
            binding.timeTextViewStatisticDatumItemCard.text = formatTimestamp(datum.timestamp, DateTimeFormat.hhmmss)
            binding.valueTextViewStatisticDatumItemCard.text = datum.value.toDisplayString(3)
            binding.unitTextViewStatisticDatumItemCard.text = binding.root.context.getString(item.unitResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StatisticDatumItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<StatisticDatumViewItem>){
        items.clear()
        items.addAll(newItems)
        items.reverse()
        notifyDataSetChanged()
    }

    data class StatisticDatumViewItem(
        val valueGrowth: ValueGrowth,
        val valueStatus: ValueStatus,
        @StringRes
        val unitResId: Int,
        val datum: StatisticDatum,
    )

    enum class ValueStatus {
        Good,
        Bad
    }

    enum class ValueGrowth {
        Up,
        Down,
    }

}