package com.rizqi.wideloc.presentation.ui.connect_via_bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.rizqi.wideloc.databinding.FragmentConnectViaBluetoothBinding
import com.rizqi.wideloc.presentation.ui.connect_via_bluetooth.adapters.ConnectViaBluetoothPagerAdapter

class ConnectViaBluetoothFragment : Fragment() {

    private var _binding: FragmentConnectViaBluetoothBinding? = null
    val binding get() = _binding!!
    private lateinit var pagerAdapter: ConnectViaBluetoothPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectViaBluetoothBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = ConnectViaBluetoothPagerAdapter(this)
        binding.contentViewPagerFragmentConnectViaBluetooth.adapter = pagerAdapter
        binding.contentViewPagerFragmentConnectViaBluetooth.isUserInputEnabled = false

        binding.contentViewPagerFragmentConnectViaBluetooth.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.stepsIndicatorFragmentConnectViaBluetooth.currentStep = position
                }
            }
        )
    }

    fun goToNextPage(){
        val currentItem = binding.contentViewPagerFragmentConnectViaBluetooth.currentItem
        val nextItem = currentItem + 1
        if (nextItem < pagerAdapter.itemCount) {
            binding.contentViewPagerFragmentConnectViaBluetooth.setCurrentItem(nextItem, true)
        }
    }
}