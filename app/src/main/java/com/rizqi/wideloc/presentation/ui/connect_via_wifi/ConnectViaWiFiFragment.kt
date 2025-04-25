package com.rizqi.wideloc.presentation.ui.connect_via_wifi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.rizqi.wideloc.databinding.FragmentConnectViaWifiBinding
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters.ConnectViaWifiPagerAdapter

class ConnectViaWiFiFragment : Fragment() {

    private var _binding: FragmentConnectViaWifiBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagerAdapter: ConnectViaWifiPagerAdapter

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
    }

    fun goToNextPage(){
        val currentItem = binding.contentViewPagerFragmentConnectViaWifi.currentItem
        val nextItem = currentItem + 1
        if (nextItem < pagerAdapter.itemCount) {
            binding.contentViewPagerFragmentConnectViaWifi.setCurrentItem(nextItem, true)
        }
    }
}