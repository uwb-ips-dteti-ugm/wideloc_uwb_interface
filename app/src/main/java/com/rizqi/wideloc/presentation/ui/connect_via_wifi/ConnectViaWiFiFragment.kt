package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.rizqi.wideloc.databinding.FragmentConnectViaWifiBinding
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.ConnectViaWifiPagerAdapter
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceViewModel

class ConnectViaWiFiFragment : Fragment() {

    private var _binding: FragmentConnectViaWifiBinding? = null
    val binding get() = _binding!!
    private lateinit var pagerAdapter: ConnectViaWifiPagerAdapter

    private val addDeviceViewModel: AddDeviceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectViaWifiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = ConnectViaWifiPagerAdapter(this)
        binding.contentViewPagerFragmentConnectViaWifi.adapter = pagerAdapter
        binding.contentViewPagerFragmentConnectViaWifi.isUserInputEnabled = false

        binding.contentViewPagerFragmentConnectViaWifi.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.stepsIndicatorFragmentConnectViaWifi.currentStep = position
                }
            }
        )

        addDeviceViewModel.jumpTopage.observe(viewLifecycleOwner){page ->
            if (page != null){
                goToPage(page)
                addDeviceViewModel.hasJumpToPage()
            }
        }
    }

    fun goToNextPage(){
        val currentItem = binding.contentViewPagerFragmentConnectViaWifi.currentItem
        val nextItem = currentItem + 1
        if (nextItem < pagerAdapter.itemCount) {
            binding.contentViewPagerFragmentConnectViaWifi.setCurrentItem(nextItem, true)
        }
    }

    fun goToPage(index: Int){
        if (index < pagerAdapter.itemCount) {
            binding.contentViewPagerFragmentConnectViaWifi.setCurrentItem(index, true)
        }
    }
}