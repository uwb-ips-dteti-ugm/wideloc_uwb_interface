package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.rizqi.wideloc.databinding.FragmentInputUrlBinding
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceViewModel
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus
import com.rizqi.wideloc.data.Result

class InputURLFragment : Fragment() {

    private var _binding: FragmentInputUrlBinding? = null
    private val binding get() = _binding!!

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputUrlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recalculateContentHeight()

        addDeviceViewModel.urlValidationResult.observe(viewLifecycleOwner){ result ->
            when(result) {
                is Result.Success -> {
                    (parentFragment as? ConnectViaWiFiFragment)?.goToNextPage()
                }
                is Result.Error -> {
                    binding.deviceUrlInputLayoutInputUrlFragment.error = result.errorMessage
                }
                else -> {}
            }
            recalculateContentHeight()
        }

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }
        binding.connectButtonInputUrlFragment.setOnClickListener {
            saveAndValidateUrl()
        }
        binding.deviceUrlInputEditTextInputUrlFragment.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO){
                saveAndValidateUrl()
                true
            } else {
                false
            }
        }
    }

    private fun saveAndValidateUrl(){
        val url = binding.deviceUrlInputEditTextInputUrlFragment.text.toString()
        addDeviceViewModel.setUrl(url)
    }

    private fun recalculateContentHeight(){
        view?.post {
            (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.recalculateHeight(
                listOf(
                    (parentFragment as? ConnectViaWiFiFragment)?.binding?.stepsIndicatorFragmentConnectViaWifi,
                    binding.root,
                ),
            )
        }
    }

}