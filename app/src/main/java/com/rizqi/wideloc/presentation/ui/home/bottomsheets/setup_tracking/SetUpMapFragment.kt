package com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking

import android.R
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.databinding.FragmentSetUpMapBinding
import com.rizqi.wideloc.domain.model.MapData
import com.rizqi.wideloc.domain.model.MapUnit
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.dialogs.AddMapDialog
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import com.rizqi.wideloc.utils.StorageUtils
import com.rizqi.wideloc.utils.ViewUtils.flipBitmap
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus
import com.rizqi.wideloc.utils.ViewUtils.rotateBitmap

class SetUpMapFragment : BaseFragment<FragmentSetUpMapBinding>(FragmentSetUpMapBinding::inflate) {

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var addMapDialog: AddMapDialog? = null

    private lateinit var mapAdapter: ArrayAdapter<String>
    private var selectedMap: MapData? = null

    private lateinit var mapUnitAdapter: ArrayAdapter<String>
    private var selectedMapUnit: MapUnit = MapUnit.M

    private var mapImageBitmap: Bitmap? = null
    private var mapImageRotation = 0f
    private var mapImageFlippedHorizontally = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (parentFragment?.parentFragment as? SetupTrackingSessionBottomSheet)?.toggleWifiInfoVisibility(
            true
        )

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){uri ->
            uri?.let {
                addMapDialog?.updateImage(it)
            }
        }
        addMapDialog = AddMapDialog(
            context = requireContext(),
            onPickImage = { pickImageLauncher.launch("image/*") },
            onSave = { name, imageUri ->
                val imageFile = StorageUtils.copyUriToInternalStorage(requireContext(), imageUri, "${name}_${System.currentTimeMillis()}.jpg")
                val imagePath = imageFile?.absolutePath
                trackingViewModel.insertMap(name, imagePath)
            }
        )

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }
        binding.addMapButtonSetUpFragment.setOnClickListener {
            addMapDialog?.show()
        }
        binding.axisStepInputEditTextSetUpMapFragment.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO){
                hideKeyboardAndClearFocus(requireActivity().currentFocus ?: binding.root)
                saveMap()
                true
            } else {
                false
            }
        }

        mapUnitAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item_1line,
            MapUnit.entries.map { it.name.lowercase() }
        )
        binding.unitAutoCompleteSetUpMapFragment.apply {
            setAdapter(mapUnitAdapter)
            setText(selectedMapUnit.name.lowercase(), false)
            setOnItemClickListener { _, _, index, _ ->
                val unit = MapUnit.entries[index]
                selectedMapUnit = unit
            }
            setOnClickListener {
                binding.unitAutoCompleteSetUpMapFragment.showDropDown()
                recalculateContentHeight()
            }
        }

        trackingViewModel.availableMaps.observe(viewLifecycleOwner) { maps ->
            mapAdapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_dropdown_item_1line,
                maps.map { it.name }
            )
            binding.selectMapAutoCompleteSetUpMapFragment.apply {
                setAdapter(mapAdapter)
                setText(selectedMap?.name, false)
                setOnItemClickListener { _, _, index, _ ->
                    val map = maps[index]
                    selectedMap = map
                    trackingViewModel.setSelectedMap(map)
                    binding.mapLayoutFragmentSetUpMap.visibility = View.VISIBLE
                    Glide.with(context)
                        .asBitmap()
                        .load(StorageUtils.getFileFromPath(map.imageUri))
                        .into(
                            object : CustomTarget<Bitmap>(){
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    mapImageBitmap = resource
                                    binding.mapImageViewFragmentSetUpMap.setImageBitmap(resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }

                            }
                        )
                    recalculateContentHeight()
                }
                setOnClickListener {
                    binding.selectMapAutoCompleteSetUpMapFragment.showDropDown()
                    recalculateContentHeight()
                }
            }
        }
        trackingViewModel.saveMapError.observe(viewLifecycleOwner){error ->
            binding.lengthInputLayoutSetUpMapFragment.error = error.length
            binding.widthInputLayoutSetUpMapFragment.error = error.width
            binding.selectMapInputLayoutSetUpMapFragment.error = error.map
            recalculateContentHeight()
        }
        trackingViewModel.saveMapResult.observe(viewLifecycleOwner){result ->
            when(result){
                is Result.Error -> Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_LONG).show()
                is Result.Loading<*> -> Unit
                is Result.Success<*> -> {
                    (parentFragment as? SetupTrackingSessionFragment)?.goToNextPage()
                }
            }
        }

        binding.rotateButtonFragmentSetUpMap.setOnClickListener {
            mapImageBitmap?.let {
                mapImageRotation = (mapImageRotation + 90f) % 360f
                mapImageBitmap = rotateBitmap(it, 90f)
                binding.mapImageViewFragmentSetUpMap.setImageBitmap(mapImageBitmap)
                recalculateContentHeight()
            }
        }

        binding.flipButtonFragmentSetUpMap.setOnClickListener {
            mapImageBitmap?.let {
                mapImageFlippedHorizontally = !mapImageFlippedHorizontally
                mapImageBitmap = flipBitmap(it)
                binding.mapImageViewFragmentSetUpMap.setImageBitmap(mapImageBitmap)
                recalculateContentHeight()
            }
        }

        binding.saveButtonFragmentSetUpMap.setOnClickListener {
            saveMap()
        }

        recalculateContentHeight()

    }

    private fun saveMap(){
        val length = binding.lengthInputEditTextSetUpMapFragment.text.toString()
        val width = binding.widthInputEditTextSetUpMapFragment.text.toString()
        val axisStepUnit = "0.5"
//        val axisStepUnit = binding.axisStepInputEditTextSetUpMapFragment.text.toString()

        trackingViewModel.saveMapSelection(
            lengthText = length,
            widthText = width,
            mapRotation = mapImageRotation,
            isFlipX = mapImageFlippedHorizontally,
            stepAxisText = axisStepUnit,
            mapUnit = selectedMapUnit
        )
    }

    private fun recalculateContentHeight() {
        view?.post {
            (parentFragment?.parentFragment as? SetupTrackingSessionBottomSheet)?.recalculateHeight(
                listOf(
                    (parentFragment as SetupTrackingSessionFragment).binding.stepsIndicatorFragmentSetupTrackingSession,
                    binding.root,
                ),
            )
        }
    }

}