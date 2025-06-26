package com.rizqi.wideloc.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rizqi.wideloc.databinding.FragmentHomeBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment
import com.rizqi.wideloc.presentation.viewmodel.TrackingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val trackingViewModel: TrackingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startRecordButtonHome.setOnClickListener {
            trackingViewModel.startObserveData()
        }
    }

}
