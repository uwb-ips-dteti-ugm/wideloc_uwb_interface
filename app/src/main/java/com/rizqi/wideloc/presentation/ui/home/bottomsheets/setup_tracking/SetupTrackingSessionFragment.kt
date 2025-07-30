package com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.rizqi.wideloc.databinding.FragmentSetupTrackingSessionBinding
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceViewModel
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking.adapters.SetupTrackingSessionPagerAdapter

class SetupTrackingSessionFragment : Fragment() {

    private var _binding: FragmentSetupTrackingSessionBinding? = null
    val binding get() = _binding!!
    private lateinit var pagerAdapter: SetupTrackingSessionPagerAdapter

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupTrackingSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = SetupTrackingSessionPagerAdapter(this)
        binding.contentViewPagerFragmentSetupTrackingSession.adapter = pagerAdapter
        binding.contentViewPagerFragmentSetupTrackingSession.isUserInputEnabled = false

        binding.contentViewPagerFragmentSetupTrackingSession.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.stepsIndicatorFragmentSetupTrackingSession.currentStep = position
                }
            }
        )

        addDeviceViewModel.jumpToPage.observe(viewLifecycleOwner){ page ->
            if (page != null){
                goToPage(page)
                addDeviceViewModel.hasJumpToPage()
            }
        }
    }

    fun goToNextPage(){
        val currentItem = binding.contentViewPagerFragmentSetupTrackingSession.currentItem
        val nextItem = currentItem + 1
        if (nextItem < pagerAdapter.itemCount) {
            binding.contentViewPagerFragmentSetupTrackingSession.setCurrentItem(nextItem, true)
        }
    }

    fun goToPage(index: Int){
        if (index < pagerAdapter.itemCount) {
            binding.contentViewPagerFragmentSetupTrackingSession.setCurrentItem(index, true)
        }
    }
}