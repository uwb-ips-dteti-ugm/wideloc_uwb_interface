package com.rizqi.wideloc.presentation.ui.connect_via_bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.FragmentTurnOnBluetoothBinding
import com.rizqi.wideloc.presentation.ui.devices.bottomsheets.add_device.AddDeviceBottomSheet
import com.rizqi.wideloc.utils.ViewUtils.hideKeyboardAndClearFocus

class TurnOnBluetoothFragment : Fragment() {

    private var _binding: FragmentTurnOnBluetoothBinding? = null
    private val binding get() = _binding!!

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (bluetoothAdapter?.isEnabled == true) {
            binding.turnOnButtonTurnOnBluetoothFragment.text = getString(R.string.next)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            enableBluetooth()
        } else {
            Toast.makeText(requireContext(), "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTurnOnBluetoothBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            (parentFragment?.parentFragment as? AddDeviceBottomSheet)?.recalculateHeight(
                listOf(
                    (parentFragment as? ConnectViaBluetoothFragment)?.binding?.stepsIndicatorFragmentConnectViaBluetooth,
                    binding.root,
                ),
            )
        }

        if (bluetoothAdapter?.isEnabled == true) {
            binding.turnOnButtonTurnOnBluetoothFragment.text = getString(R.string.next)
            binding.statusTextViewTurnOnBluetoothFragment.text =
                getString(R.string.the_bluetooth_is_on)
        }

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus(requireActivity().currentFocus ?: it)
        }

        binding.turnOnButtonTurnOnBluetoothFragment.setOnClickListener {
            if (bluetoothAdapter?.isEnabled == false) {
                checkAndRequestBluetoothPermission()
            } else {
                (parentFragment as? ConnectViaBluetoothFragment)?.goToNextPage()
            }
        }
    }

    private fun checkAndRequestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hasPermission = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                enableBluetooth()
            }
        } else {
            enableBluetooth()
        }
    }

    private fun enableBluetooth() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
