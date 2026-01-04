package com.mainstation.app.ui.screens.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.Slider
import com.mainstation.app.R
import com.mainstation.app.data.model.Booking
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date
import com.mainstation.app.databinding.FragmentBookingBinding

@AndroidEntryPoint
class BookingFragment : Fragment() {

    private var hourlyRate: Double = 50000.0 // Default or passed via args
    private var duration: Int = 2
    private val viewModel: BookingViewModel by viewModels()

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments
        val itemName = arguments?.getString("itemName") ?: "Select Duration"
        hourlyRate = arguments?.getDouble("hourlyRate") ?: 50000.0

        binding.tvSelectedItemName.text = itemName

        // Branch Setup
        val branches = listOf("Yogyakarta", "Bantul", "Sleman", "Gunung Kidul")
        val adapter = android.widget.ArrayAdapter(requireContext(), R.layout.item_spinner_white, branches)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBranch.adapter = adapter

        // Init UI
        updateTotal()

        binding.sliderDuration.addOnChangeListener { _, value, _ ->
            duration = value.toInt()
            binding.tvDurationDisplay.text = "$duration Hours"
            updateTotal()
        }

        binding.btnConfirmBooking.setOnClickListener {
            val selectedBranch = binding.spinnerBranch.selectedItem.toString()
            
            // Retrieve IDs
            val selectedRoomId = arguments?.getString("roomId") 
            val selectedConsoleId = arguments?.getString("consoleId")
            
            // Validation: Ensure at least one item is selected
            if (selectedRoomId == null && selectedConsoleId == null) {
                Toast.makeText(context, "Error: No item selected!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val totalCost = duration * hourlyRate
            val formattedCost = java.text.NumberFormat.getIntegerInstance().format(totalCost)

            // Show Confirmation Dialog
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Booking")
                .setMessage("Are you sure you want to book $itemName for $duration hours?\nTotal: Rp $formattedCost")
                .setPositiveButton("Confirm") { _, _ ->
                     // Debug Toast
                     // Toast.makeText(context, "Booking ID: ${selectedConsoleId ?: selectedRoomId}", Toast.LENGTH_SHORT).show()

                    viewModel.submitBooking(
                        roomId = selectedRoomId,
                        consoleId = selectedConsoleId,
                        duration = duration,
                        hourlyRate = hourlyRate,
                        location = selectedBranch
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bookingResult.collect { result ->
                if (result.isSuccess) {
                     Toast.makeText(context, "Booking Confirmed!", Toast.LENGTH_LONG).show()
                     val navOptions = androidx.navigation.NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_catalog, false)
                        .build()
                     findNavController().navigate(R.id.navigation_profile, null, navOptions)
                } else {
                     Toast.makeText(context, "Booking Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTotal() {
        val total = duration * hourlyRate
        binding.tvTotalPrice.text = "Rp ${java.text.NumberFormat.getIntegerInstance().format(total)}"
    }
}
