package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.databinding.FragmentSetUpDeviceBinding
import com.rizqi.wideloc.presentation.ui.connect_via_bluetooth.ConnectViaBluetoothFragment
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceViewModel
import com.rizqi.wideloc.utils.StorageUtils
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus

class SetUpDeviceFragment : Fragment() {

    private var _binding: FragmentSetUpDeviceBinding? = null
    private val binding get() = _binding!!

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.deviceImageImageViewFragmentSetUpDevice.setImageURI(it)
            binding.deviceImageCardViewLayoutFragmentSetUpDevice.visibility = View.VISIBLE
            binding.addImageLayoutFragmentSetUpDevice.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetUpDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recalculateContentHeight()
        addDeviceViewModel.nameValidationResult.observe(viewLifecycleOwner){result ->
            when(result) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    binding.deviceNameInputLayoutSetUpDeviceFragment.error = result.errorMessage
                }
                else -> {}
            }
            recalculateContentHeight()
        }
        addDeviceViewModel.saveDeviceResult.observe(viewLifecycleOwner){result ->
            when(result) {
                is Result.Success -> {
                    when (parentFragment) {
                        is ConnectViaWiFiFragment -> (parentFragment as ConnectViaWiFiFragment).goToNextPage()
                        is ConnectViaBluetoothFragment -> (parentFragment as ConnectViaBluetoothFragment).goToNextPage()
                    }
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
            recalculateContentHeight()
        }

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }

        binding.saveButtonFragmentSetUpDevice.setOnClickListener {
            saveDeviceSetup()
        }

        binding.roleInputEditTextSetUpDeviceFragment.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO){
                saveDeviceSetup()
                true
            } else {
                false
            }
        }

        binding.addImageLayoutFragmentSetUpDevice.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.editImageButtonFragmentSetUpDevice.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.deleteImageButtonFragmentSetUpDevice.setOnClickListener {
            selectedImageUri = null
            binding.deviceImageImageViewFragmentSetUpDevice.setImageDrawable(null)
            binding.deviceImageCardViewLayoutFragmentSetUpDevice.visibility = View.GONE
            binding.addImageLayoutFragmentSetUpDevice.visibility = View.VISIBLE
        }

    }

    private fun saveDeviceSetup(){
        val name = binding.deviceNameInputEditTextSetUpDeviceFragment.text.toString()
        val offsetX = binding.xOffsetInputEditTextSetUpDeviceFragment.text.toString()
        val offsetY = binding.yOffsetInputEditTextSetUpDeviceFragment.text.toString()
        val offsetZ = binding.zOffsetInputEditTextSetUpDeviceFragment.text.toString()
        val role = binding.roleInputEditTextSetUpDeviceFragment.text.toString()
        val imageFile = StorageUtils.copyUriToInternalStorage(requireContext(), selectedImageUri, "${name}_${System.currentTimeMillis()}.jpg")
        val imagePath = imageFile?.absolutePath
        addDeviceViewModel.setDeviceSetup(
            name = name,
            offsetX = offsetX,
            offsetY = offsetY,
            offsetZ = offsetZ,
            role = role,
            imagePath = imagePath,
        )
    }

    private fun recalculateContentHeight() {
        view?.post {
            (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.recalculateHeight(
                listOf(
                    when (parentFragment) {
                        is ConnectViaWiFiFragment -> (parentFragment as ConnectViaWiFiFragment).binding.stepsIndicatorFragmentConnectViaWifi
                        is ConnectViaBluetoothFragment -> (parentFragment as ConnectViaBluetoothFragment).binding.stepsIndicatorFragmentConnectViaBluetooth
                        else -> null
                    },
                    binding.root,
                ),
            )
        }
    }

}