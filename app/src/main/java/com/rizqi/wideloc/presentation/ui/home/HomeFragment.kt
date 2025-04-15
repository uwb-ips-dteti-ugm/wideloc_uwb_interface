package com.rizqi.wideloc.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rizqi.wideloc.databinding.FragmentHomeBinding
import com.rizqi.wideloc.presentation.ui.BaseFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "View created OK", Toast.LENGTH_SHORT).show()
        binding.startRecordButtonHome.text = "Testing"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
