package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.ItemDeviceSetLayoutBinding
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel.CoordinateTarget
import java.util.Locale

class ClientSetLayoutAdapter(
    private val trackingViewModel: TrackingViewModel,
    private val recalculateContentHeight: () -> Unit,
) : ListAdapter<TrackingViewModel.DeviceCoordinate, ClientSetLayoutAdapter.ClientLayoutViewHolder>(DIFF_CALLBACK) {


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrackingViewModel.DeviceCoordinate>() {
            override fun areItemsTheSame(
                oldItem: TrackingViewModel.DeviceCoordinate,
                newItem: TrackingViewModel.DeviceCoordinate
            ): Boolean {
                return oldItem.deviceData?.id == newItem.deviceData?.id
            }

            override fun areContentsTheSame(
                oldItem: TrackingViewModel.DeviceCoordinate,
                newItem: TrackingViewModel.DeviceCoordinate
            ): Boolean {
                return oldItem.coordinate.areContentsTheSame(newItem.coordinate)
            }
        }
    }

    inner class ClientLayoutViewHolder(val binding: ItemDeviceSetLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
                    if (axis == "x") {
                        trackingViewModel.setX(target, delta = 0.1, isOffset = isOffset, deviceData = deviceData)
                    } else {
                        trackingViewModel.setY(target, delta = 0.1, isOffset = isOffset, deviceData = deviceData)
                    }
                }
            }

            downButton.setOnClickListener {
                it.post {
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
            val formattedValue = String.format(Locale.US, "%.5f", newValue).trimEnd('0').trimEnd('.')
            val currentText = editText.text.toString().trim()
            if (currentText != formattedValue && !editText.hasFocus()) {
                editText.setText(formattedValue)
            }
            recalculateContentHeight()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientLayoutViewHolder {
        val binding = ItemDeviceSetLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientLayoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientLayoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
