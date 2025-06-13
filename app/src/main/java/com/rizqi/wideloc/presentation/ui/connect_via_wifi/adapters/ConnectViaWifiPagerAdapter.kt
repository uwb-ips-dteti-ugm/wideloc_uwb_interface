package com.rizqi.wideloc.presentation.ui.connect_via_wifi.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.ConnectDeviceWifiFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.InputURLFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.NetworkConfigFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.SetUpDeviceFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.TestConnectionFragment

class ConnectViaWifiPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val pages = listOf(
        NetworkConfigFragment(),
        SetUpDeviceFragment(),
        ConnectDeviceWifiFragment(),
//        InputURLFragment(),
//        TestConnectionFragment(),
    )

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }
}