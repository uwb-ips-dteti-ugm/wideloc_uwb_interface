package com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking.ConnectNetworkFragment
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking.SelectDevicesFragment
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking.SetLayoutFragment
import com.rizqi.wideloc.presentation.ui.home.bottomsheets.setup_tracking.SetUpMapFragment

class SetupTrackingSessionPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val pages = listOf(
        SelectDevicesFragment(),
//        ConnectNetworkFragment(),
        SetUpMapFragment(),
        SetLayoutFragment(),
    )

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }
}