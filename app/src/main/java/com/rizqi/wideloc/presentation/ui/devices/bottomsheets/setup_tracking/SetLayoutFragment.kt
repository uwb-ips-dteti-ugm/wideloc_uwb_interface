package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.FragmentSetLayoutBinding
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.adapters.ClientSetLayoutAdapter
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.adapters.ClientSetLayoutCustomAdapter
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel.CoordinateTarget
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus
import com.rizqi.wideloc.utils.toDisplayString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class SetLayoutFragment :
    BaseFragment<FragmentSetLayoutBinding>(FragmentSetLayoutBinding::inflate) {

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    private lateinit var clientSetLayoutCustomAdapter: ClientSetLayoutCustomAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (parentFragment?.parentFragment as? SetupTrackingSessionBottomSheet)?.toggleWifiInfoVisibility(false)

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }

        clientSetLayoutCustomAdapter = ClientSetLayoutCustomAdapter(
            trackingViewModel = trackingViewModel,
            linearLayout = binding.clientsCoordinateLayoutFragmentSetLayout,
            recalculateContentHeight = ::recalculateContentHeight
        )

        // Static read-only X/Y fields (Server only)
        setupReadonlyCoordinateField(
            editText = binding.xServerInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            hint = getXHint(),
            inputLayout = binding.xServerInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
        )
        setupReadonlyCoordinateField(
            editText = binding.yServerInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            hint = getYHint(),
            inputLayout = binding.yServerInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
        )

        // Editable fields setup
        setupAllEditableCoordinateFields()

        // LiveData observer
        trackingViewModel.layoutInitialCoordinate.observe(viewLifecycleOwner) { layout ->
            val server = layout.serverCoordinate.coordinate
            binding.xServerInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown.setText(server.x.toDisplayString())
            binding.yServerInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown.setText(server.y.toDisplayString())

            updateEditableFieldIfNeeded(
                editText = binding.xOffsetServerInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
                newValue = server.xOffset
            )
            updateEditableFieldIfNeeded(
                editText = binding.yOffsetServerInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
                newValue = server.yOffset
            )

            val anchor = layout.anchorCoordinate.coordinate
            updateEditableFieldIfNeeded(
                editText = binding.xAnchorInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
                newValue = anchor.x
            )
            updateEditableFieldIfNeeded(
                editText = binding.xOffsetAnchorInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
                newValue = anchor.xOffset
            )
            updateEditableFieldIfNeeded(
                editText = binding.yAnchorInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
                newValue = anchor.y
            )
            updateEditableFieldIfNeeded(
                editText = binding.yOffsetAnchorInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
                newValue = anchor.yOffset
            )

            val map = layout.mapCoordinate
            updateEditableFieldIfNeeded(
                editText = binding.xOffsetMapInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
                newValue = map.xOffset
            )
            updateEditableFieldIfNeeded(
                editText = binding.yOffsetMapInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
                newValue = map.yOffset
            )

            val clients = layout.clientsCoordinate
            clientSetLayoutCustomAdapter.submitList(clients)

            recalculateContentHeight()
        }

        trackingViewModel.initLayoutInitialCoordinate()

        recalculateContentHeight()
    }

    private fun setupAllEditableCoordinateFields() {
        // SERVER OFFSET
        setupEditableCoordinateField(
            target = CoordinateTarget.SERVER,
            axis = "x",
            isOffset = true,
            editText = binding.xOffsetServerInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            inputLayout = binding.xOffsetServerInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
            upButton = binding.xOffsetServerInputLayoutSetUpMapFragment.upButtonItemInputUpDown,
            downButton = binding.xOffsetServerInputLayoutSetUpMapFragment.downButtonItemInputUpDown,
            hint = getXOffsetHint()
        )

        setupEditableCoordinateField(
            target = CoordinateTarget.SERVER,
            axis = "y",
            isOffset = true,
            editText = binding.yOffsetServerInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            inputLayout = binding.yOffsetServerInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
            upButton = binding.yOffsetServerInputLayoutSetUpMapFragment.upButtonItemInputUpDown,
            downButton = binding.yOffsetServerInputLayoutSetUpMapFragment.downButtonItemInputUpDown,
            hint = getYOffsetHint()
        )

        // ANCHOR
        setupEditableCoordinateField(
            target = CoordinateTarget.ANCHOR,
            axis = "x",
            isOffset = false,
            editText = binding.xAnchorInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            inputLayout = binding.xAnchorInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
            upButton = binding.xAnchorInputLayoutSetUpMapFragment.upButtonItemInputUpDown,
            downButton = binding.xAnchorInputLayoutSetUpMapFragment.downButtonItemInputUpDown,
            hint = getXHint()
        )

        setupEditableCoordinateField(
            target = CoordinateTarget.ANCHOR,
            axis = "x",
            isOffset = true,
            editText = binding.xOffsetAnchorInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            inputLayout = binding.xOffsetAnchorInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
            upButton = binding.xOffsetAnchorInputLayoutSetUpMapFragment.upButtonItemInputUpDown,
            downButton = binding.xOffsetAnchorInputLayoutSetUpMapFragment.downButtonItemInputUpDown,
            hint = getXOffsetHint()
        )

        setupEditableCoordinateField(
            target = CoordinateTarget.ANCHOR,
            axis = "y",
            isOffset = false,
            editText = binding.yAnchorInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            inputLayout = binding.yAnchorInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
            upButton = binding.yAnchorInputLayoutSetUpMapFragment.upButtonItemInputUpDown,
            downButton = binding.yAnchorInputLayoutSetUpMapFragment.downButtonItemInputUpDown,
            hint = getYHint()
        )

        setupEditableCoordinateField(
            target = CoordinateTarget.ANCHOR,
            axis = "y",
            isOffset = true,
            editText = binding.yOffsetAnchorInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            inputLayout = binding.yOffsetAnchorInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
            upButton = binding.yOffsetAnchorInputLayoutSetUpMapFragment.upButtonItemInputUpDown,
            downButton = binding.yOffsetAnchorInputLayoutSetUpMapFragment.downButtonItemInputUpDown,
            hint = getYOffsetHint()
        )

        // MAP OFFSET
        setupEditableCoordinateField(
            target = CoordinateTarget.MAP,
            axis = "x",
            isOffset = true,
            editText = binding.xOffsetMapInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            inputLayout = binding.xOffsetMapInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
            upButton = binding.xOffsetMapInputLayoutSetUpMapFragment.upButtonItemInputUpDown,
            downButton = binding.xOffsetMapInputLayoutSetUpMapFragment.downButtonItemInputUpDown,
            hint = getXOffsetHint()
        )

        setupEditableCoordinateField(
            target = CoordinateTarget.MAP,
            axis = "y",
            isOffset = true,
            editText = binding.yOffsetMapInputLayoutSetUpMapFragment.valueInputEditTextItemInputUpDown,
            inputLayout = binding.yOffsetMapInputLayoutSetUpMapFragment.valueInputLayoutItemInputUpDown,
            upButton = binding.yOffsetMapInputLayoutSetUpMapFragment.upButtonItemInputUpDown,
            downButton = binding.yOffsetMapInputLayoutSetUpMapFragment.downButtonItemInputUpDown,
            hint = getYOffsetHint()
        )
    }

    private fun setupEditableCoordinateField(
        target: CoordinateTarget,
        deviceData: DeviceData? = null,
        axis: String,
        isOffset: Boolean,
        editText: EditText,
        inputLayout: TextInputLayout,
        upButton: View,
        downButton: View,
        hint: String
    ) {
        inputLayout.hint = hint

        editText.doAfterTextChanged { text ->
            trackingViewModel.run {
                if (axis == "x") setX(
                    target = target,
                    value = text.toString(),
                    isOffset = isOffset,
                    deviceData = deviceData,
                )
                else setY(
                    target = target,
                    value = text.toString(),
                    isOffset = isOffset,
                    deviceData = deviceData
                )
            }
        }

        upButton.setOnClickListener {
            trackingViewModel.run {
                if (axis == "x") setX(
                    target = target,
                    delta = 0.1,
                    isOffset = isOffset,
                    deviceData = deviceData
                )
                else setY(
                    target = target,
                    delta = 0.1,
                    isOffset = isOffset,
                    deviceData = deviceData
                )
            }
        }

        downButton.setOnClickListener {
            trackingViewModel.run {
                if (axis == "x") setX(
                    target = target,
                    delta = -0.1,
                    isOffset = isOffset,
                    deviceData = deviceData
                )
                else setY(
                    target = target,
                    delta = -0.1,
                    isOffset = isOffset,
                    deviceData = deviceData
                )
            }
        }
    }

    private fun setupReadonlyCoordinateField(editText: EditText, inputLayout: TextInputLayout, hint: String) {
        editText.isEnabled = false
        inputLayout.hint = hint
    }

    private fun updateEditableFieldIfNeeded(editText: EditText, newValue: Double) {
        val formattedValue = String.format(Locale.US, "%.5f", newValue).trimEnd('0').trimEnd('.')
        val currentText = editText.text.toString().trim()

        if (currentText != formattedValue) {
            editText.setText(formattedValue)
        }
    }

    private fun recalculateContentHeight() {
        view?.post {
            (parentFragment?.parentFragment as? SetupTrackingSessionBottomSheet)?.recalculateHeight(
                listOf(
                    (parentFragment as SetupTrackingSessionFragment).binding.stepsIndicatorFragmentSetupTrackingSession,
                    binding.root,
                )
            )
        }
    }

    private fun getXHint() = requireContext().getString(R.string.x)

    private fun getXOffsetHint() = requireContext().getString(R.string.x_offset)

    private fun getYHint() = requireContext().getString(R.string.y)

    private fun getYOffsetHint() = requireContext().getString(R.string.y_offset)
}