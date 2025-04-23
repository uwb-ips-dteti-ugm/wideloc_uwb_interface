package com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rizqi.wideloc.databinding.FragmentSelectProtocolBinding

class SelectProtocolFragment : Fragment() {

    private var _binding: FragmentSelectProtocolBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectProtocolBinding.inflate(inflater, container, false)
        return binding.root
    }
}