package com.rizqi.wideloc.presentation.ui.home.bottomsheets.statistics.tableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.TableViewCellLayoutBinding
import com.rizqi.wideloc.databinding.TableViewColumnHeaderLayoutBinding
import com.rizqi.wideloc.databinding.TableViewCornerLayoutBinding
import com.rizqi.wideloc.databinding.TableViewRowHeaderLayoutBinding

class TableViewAdapter : AbstractTableAdapter<ColumnHeader, RowHeader, Cell>() {

    inner  class CellViewHolder(val binding: TableViewCellLayoutBinding) : AbstractViewHolder(binding.root){

        fun bind(cell: Cell?){
            binding.cellData.text = cell?.value
//            binding.cellContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
//            binding.cellContainer.requestLayout()
        }
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val binding = TableViewCellLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CellViewHolder(binding)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: Cell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        (holder as CellViewHolder).bind(cellItemModel)
    }



    inner class ColumnViewHolder(val binding: TableViewColumnHeaderLayoutBinding) : AbstractViewHolder(binding.root) {

        fun bind(columnHeader: ColumnHeader?) {
            binding.columnHeaderTextView.text = columnHeader?.value
            binding.columnHeaderContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            binding.columnHeaderContainer.requestLayout()
            binding.root.setBackgroundResource(R.color.cell_background_color)
        }
    }


    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val binding = TableViewColumnHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColumnViewHolder(binding)
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        (holder as ColumnViewHolder).bind(columnHeaderItemModel)
    }



    inner  class RowViewHolder(val binding: TableViewRowHeaderLayoutBinding) : AbstractViewHolder(binding.root){

        fun bind(rowHeader: RowHeader?){
            binding.rowHeaderTextView.text = rowHeader?.value
//            binding.rowHeaderContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
//            binding.rowHeaderContainer.requestLayout()
            binding.root.setBackgroundResource(R.color.cell_background_color)
        }
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val binding = TableViewRowHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding)
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    ) {
        (holder as RowViewHolder).bind(rowHeaderItemModel)
    }


    override fun onCreateCornerView(parent: ViewGroup): View {
        return TableViewCornerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
    }

}