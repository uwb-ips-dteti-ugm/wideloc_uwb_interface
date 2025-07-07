package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputLayout
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.DeviceTagBinding
import com.rizqi.wideloc.databinding.FragmentSetLayoutBinding
import com.rizqi.wideloc.domain.model.DeviceData
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.adapters.ClientSetLayoutAdapter
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.adapters.ClientSetLayoutCustomAdapter
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel.CoordinateTarget
import com.rizqi.wideloc.utils.StorageUtils
import com.rizqi.wideloc.utils.ViewUtils.flipBitmap
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus
import com.rizqi.wideloc.utils.ViewUtils.rotateBitmap
import com.rizqi.wideloc.utils.toDisplayString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class SetLayoutFragment :
    BaseFragment<FragmentSetLayoutBinding>(FragmentSetLayoutBinding::inflate) {

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    private lateinit var clientSetLayoutCustomAdapter: ClientSetLayoutCustomAdapter

    private val deviceTags = mutableListOf<Pair<TrackingViewModel.DeviceCoordinate, DeviceTagBinding>>()

    private val scalingFactor = 100f

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
            addDeviceTagToCartesianView(layout.serverCoordinate)

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
            addDeviceTagToCartesianView(layout.anchorCoordinate)

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
            clients.forEach {
                addDeviceTagToCartesianView(it)
            }

            recalculateContentHeight()
        }
        trackingViewModel.initLayoutInitialCoordinate()
        trackingViewModel.mapCombinedWithTransform.observe(viewLifecycleOwner){ (mapData, mapTransform) ->
            Glide.with(requireContext())
                .asBitmap()
                .load(mapData?.imageUri?.let { StorageUtils.getFileFromPath(it) })
                .error(R.drawable.map_dummy)
                .into(
                    object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            var transformedBitmap = resource
                            mapTransform?.let {
                                if (it.isFlipX){
                                    transformedBitmap = flipBitmap(transformedBitmap)
                                }
                                transformedBitmap = rotateBitmap(transformedBitmap, it.rotation)
                            }

                            // Create ImageView dynamically
                            val bitmapWidth = transformedBitmap.width
                            val bitmapHeight = transformedBitmap.height

                            val transformWidth = mapTransform?.width ?: bitmapWidth.toDouble()
                            val transformHeight = mapTransform?.length ?: bitmapHeight.toDouble()

                            val cartesianWidth = binding.cartesianViewFragmentSetLayout.width
                            val cartesianHeight = binding.cartesianViewFragmentSetLayout.height

                            // Calculate scaling factor to fit image inside CartesianView
                            val scaleX = cartesianWidth / transformWidth
                            val scaleY = cartesianHeight / transformHeight
                            val scale = minOf(scaleX, scaleY).toFloat()

                            // Final layout dimensions after scaling
                            val scaledWidth = (transformWidth * scale).toInt()
                            val scaledHeight = (transformHeight * scale).toInt()

                            val matrix = Matrix().apply {
                                val cropScaleX = scaledWidth.toFloat() / bitmapWidth
                                val cropScaleY = scaledHeight.toFloat() / bitmapHeight
                                postScale(cropScaleX, cropScaleY)
                            }

                            // Set background using the matrix
                            binding.cartesianViewFragmentSetLayout.setMapBackground(transformedBitmap, matrix)

                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                    }
                )
        }

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
        val formattedValue = newValue.toDisplayString()
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

    private fun addDeviceTagToCartesianView(deviceCoordinate: TrackingViewModel.DeviceCoordinate) {
        val tagBinding = DeviceTagBinding.inflate(layoutInflater, null, false)

        val deviceName = deviceCoordinate.deviceData?.name ?: "Unknown"
        val deviceId = deviceCoordinate.deviceData?.id ?: return // ID is required

        val x = deviceCoordinate.coordinate.x
        val y = deviceCoordinate.coordinate.y

        tagBinding.nameTextViewDeviceTag.text = deviceName
        tagBinding.coordinateTextViewDeviceTag.text = getString(
            R.string.coordinate,
            x.toDisplayString(),
            y.toDisplayString()
        )

        // Save for later (if needed)
        deviceTags.add(deviceCoordinate to tagBinding)

        tagBinding.root.scaleX = 0.6f
        tagBinding.root.scaleY = 0.6f

        binding.cartesianViewFragmentSetLayout.addOrUpdatePoint(
            id = deviceId,
            view = tagBinding.root,
            x = x,
            y = y
        )
    }

}