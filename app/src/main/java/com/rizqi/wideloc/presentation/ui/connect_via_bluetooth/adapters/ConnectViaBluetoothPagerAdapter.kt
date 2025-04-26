package com.rizqi.wideloc.presentation.ui.connect_via_bluetooth.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rizqi.wideloc.presentation.ui.connect_via_bluetooth.PairingDeviceFragment
import com.rizqi.wideloc.presentation.ui.connect_via_bluetooth.TurnOnBluetoothFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.InputURLFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.SetUpDeviceFragment
import com.rizqi.wideloc.presentation.ui.connect_via_wifi.TestConnectionFragment

class ConnectViaBluetoothPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val pages = listOf(
        TurnOnBluetoothFragment(),
        PairingDeviceFragment(),
        SetUpDeviceFragment(),
        TestConnectionFragment(),
    )

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }
}