package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.adapters

import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.android.material.textfield.TextInputLayout
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.ItemDeviceSetLayoutBinding
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel.CoordinateTarget
import com.rizqi.wideloc.utils.toDisplayString
import java.util.Locale

class ClientSetLayoutCustomAdapter(
    private val trackingViewModel: TrackingViewModel,
    private val linearLayout: LinearLayout,
    private val recalculateContentHeight: () -> Unit
) {

    private val devicesCoordinate = mutableListOf<TrackingViewModel.DeviceCoordinate>()
    private val viewHolders = mutableListOf<ClientLayoutViewHolder>()

    fun submitList(newList: List<TrackingViewModel.DeviceCoordinate>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = devicesCoordinate.size
            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return devicesCoordinate[oldItemPosition].deviceData?.id == newList[newItemPosition].deviceData?.id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return devicesCoordinate[oldItemPosition].coordinate.areContentsTheSame(newList[newItemPosition].coordinate)
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        val oldList = devicesCoordinate.toList()

        devicesCoordinate.clear()
        devicesCoordinate.addAll(newList)

        // Apply changes to LinearLayout
        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                for (i in 0 until count) {
                    val newItem = devicesCoordinate[position + i]
                    val binding = ItemDeviceSetLayoutBinding.inflate(LayoutInflater.from(linearLayout.context), linearLayout, false)
                    val viewHolder = ClientLayoutViewHolder(binding)
                    viewHolder.bind(newItem)
                    viewHolders.add(position + i, viewHolder)
                    linearLayout.addView(viewHolder.binding.root, position + i)
                }
            }

            override fun onRemoved(position: Int, count: Int) {
                for (i in 0 until count) {
                    viewHolders.removeAt(position)
                    linearLayout.removeViewAt(position)
                }
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                val vh = viewHolders.removeAt(fromPosition)
                val view = linearLayout.getChildAt(fromPosition)
                linearLayout.removeViewAt(fromPosition)
                viewHolders.add(toPosition, vh)
                linearLayout.addView(view, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                for (i in 0 until count) {
                    val updatedItem = devicesCoordinate[position + i]
                    viewHolders[position + i].bind(updatedItem)
                }
            }
        })
    }

    inner class ClientLayoutViewHolder(val binding: ItemDeviceSetLayoutBinding){

        fun bind(deviceCoordinate: TrackingViewModel.DeviceCoordinate) {
            binding.titleTextViewItemDeviceSetLayout.text = deviceCoordinate.deviceData?.name

            setupEditableCoordinateField(
                CoordinateTarget.CLIENT, deviceCoordinate.deviceData, "x", false,
                binding.xInputItemDeviceSetLayout.valueInputEditTextItemInputUpDown,
                binding.xInputItemDeviceSetLayout.valueInputLayoutItemInputUpDown,
                binding.xInputItemDeviceSetLayout.upButtonItemInputUpDown,
                binding.xInputItemDeviceSetLayout.downButtonItemInputUpDown,
                getXHint()
            )

            setupEditableCoordinateField(
                CoordinateTarget.CLIENT, deviceCoordinate.deviceData, "x", true,
                binding.xOffsetInputItemDeviceSetLayout.valueInputEditTextItemInputUpDown,
                binding.xOffsetInputItemDeviceSetLayout.valueInputLayoutItemInputUpDown,
                binding.xOffsetInputItemDeviceSetLayout.upButtonItemInputUpDown,
                binding.xOffsetInputItemDeviceSetLayout.downButtonItemInputUpDown,
                getXOffsetHint()
            )

            setupEditableCoordinateField(
                CoordinateTarget.CLIENT, deviceCoordinate.deviceData, "y", false,
                binding.yInputItemDeviceSetLayout.valueInputEditTextItemInputUpDown,
                binding.yInputItemDeviceSetLayout.valueInputLayoutItemInputUpDown,
                binding.yInputItemDeviceSetLayout.upButtonItemInputUpDown,
                binding.yInputItemDeviceSetLayout.downButtonItemInputUpDown,
                getYHint()
            )

            setupEditableCoordinateField(
                CoordinateTarget.CLIENT, deviceCoordinate.deviceData, "y", true,
                binding.yOffsetInputItemDeviceSetLayout.valueInputEditTextItemInputUpDown,
                binding.yOffsetInputItemDeviceSetLayout.valueInputLayoutItemInputUpDown,
                binding.yOffsetInputItemDeviceSetLayout.upButtonItemInputUpDown,
                binding.yOffsetInputItemDeviceSetLayout.downButtonItemInputUpDown,
                getYOffsetHint()
            )

            updateEditableFieldIfNeeded(binding.xInputItemDeviceSetLayout.valueInputEditTextItemInputUpDown, deviceCoordinate.coordinate.x)
            updateEditableFieldIfNeeded(binding.xOffsetInputItemDeviceSetLayout.valueInputEditTextItemInputUpDown, deviceCoordinate.coordinate.xOffset)
            updateEditableFieldIfNeeded(binding.yInputItemDeviceSetLayout.valueInputEditTextItemInputUpDown, deviceCoordinate.coordinate.y)
            updateEditableFieldIfNeeded(binding.yOffsetInputItemDeviceSetLayout.valueInputEditTextItemInputUpDown, deviceCoordinate.coordinate.yOffset)
        }

        private fun getXHint() = binding.root.context.getString(R.string.x)
        private fun getXOffsetHint() = binding.root.context.getString(R.string.x_offset)
        private fun getYHint() = binding.root.context.getString(R.string.y)
        private fun getYOffsetHint() = binding.root.context.getString(R.string.y_offset)

        private fun setupEditableCoordinateField(
            target: CoordinateTarget,
            deviceData: DeviceData?,
            axis: String,
            isOffset: Boolean,
            editText: EditText,
            inputLayout: TextInputLayout,
            upButton: View,
            downButton: View,
            hint: String
        ) {
            inputLayout.hint = hint

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val value = editText.text.toString()
                    editText.post {
                        if (axis == "x") {
                            trackingViewModel.setX(target, value = value, isOffset = isOffset, deviceData = deviceData)
                        } else {
                            trackingViewModel.setY(target, value = value, isOffset = isOffset, deviceData = deviceData)
                        }
                    }
                }
            }

            upButton.setOnClickListener {
                it.post {
                    editText.clearFocus()
                    hideKeyboard(editText)

                    if (axis == "x") {
                        trackingViewModel.setX(target, delta = 0.1, isOffset = isOffset, deviceData = deviceData)
                    } else {
                        trackingViewModel.setY(target, delta = 0.1, isOffset = isOffset, deviceData = deviceData)
                    }
                }
            }

            downButton.setOnClickListener {
                it.post {
                    editText.clearFocus()
                    hideKeyboard(editText)

                    if (axis == "x") {
                        trackingViewModel.setX(target, delta = -0.1, isOffset = isOffset, deviceData = deviceData)
                    } else {
                        trackingViewModel.setY(target, delta = -0.1, isOffset = isOffset, deviceData = deviceData)
                    }
                }
            }

            recalculateContentHeight()
        }

        private fun updateEditableFieldIfNeeded(editText: EditText, newValue: Double) {
            val formattedValue = newValue.toDisplayString()
            val currentText = editText.text.toString().trim()
            if (currentText != formattedValue && !editText.hasFocus()) {
                editText.setText(formattedValue)
            }
            recalculateContentHeight()
        }

        private fun hideKeyboard(view: View) {
            val imm = view.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

    }
}