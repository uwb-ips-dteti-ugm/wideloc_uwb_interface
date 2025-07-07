package com.rizqi.wideloc.presentation.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.rizqi.wideloc.data.Result
import com.rizqi.wideloc.databinding.FragmentHomeBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.setup_tracking.SetupTrackingSessionBottomSheet
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val trackingViewModel: TrackingViewModel by activityViewModels()

    private val setupTrackingSessionBottomSheet = SetupTrackingSessionBottomSheet()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackingViewModel.observeResult.observe(viewLifecycleOwner){result ->
            when(result){
                is Result.Error -> Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_LONG).show()
                is Result.Loading<*> -> Unit
                is Result.Success<*> -> Unit
                null -> Unit
            }
        }

        binding.startRecordButtonHome.setOnClickListener {
            setupTrackingSessionBottomSheet.show(parentFragmentManager, setupTrackingSessionBottomSheet.tag)
        }

    }

}
