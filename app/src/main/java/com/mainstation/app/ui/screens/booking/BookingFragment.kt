package com.mainstation.app.ui.screens.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mainstation.app.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.mainstation.app.databinding.FragmentBookingBinding

@AndroidEntryPoint
class BookingFragment : Fragment() {

    private var hourlyRate: Double = 50000.0
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

        // Retrieve arguments - Sesuaikan dengan tipe float di nav_graph
        val itemName = arguments?.getString("itemName") ?: "Select Duration"
        val rateArg = arguments?.getFloat("hourlyRate", -1f) ?: -1f
        
        hourlyRate = if (rateArg != -1f) rateArg.toDouble() else 50000.0

        binding.tvSelectedItemName.text = itemName
        
        val currencyFormat = java.text.NumberFormat.getIntegerInstance(java.util.Locale("id", "ID"))
        binding.tvSelectedItemPrice.text = "Rp ${currencyFormat.format(hourlyRate)}/hr"
        updateTotal()

        binding.sliderDuration.addOnChangeListener { _, value, _ ->
            duration = value.toInt()
            binding.tvDurationDisplay.text = "$duration Hours"
            updateTotal()
        }

        binding.etDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val datePicker = android.app.DatePickerDialog(requireContext(), { _, year, month, day ->
                val selectedDate = String.format("%d-%02d-%02d", year, month + 1, day)
                binding.etDate.setText(selectedDate)
            }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        binding.etTime.setOnClickListener {
             val calendar = java.util.Calendar.getInstance()
             val timePicker = android.app.TimePickerDialog(requireContext(), { _, hour, minute ->
                 val formattedTime = String.format("%02d:%02d", hour, minute)
                 binding.etTime.setText(formattedTime)
             }, calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE), true)
             timePicker.show()
        }

        binding.btnConfirmBooking.setOnClickListener {
            val selectedBranch = binding.spinnerBranch.selectedItem?.toString() ?: "Unknown"
            val date = binding.etDate.text.toString()
            val time = binding.etTime.text.toString()
            
            val selectedRoomId = arguments?.getString("roomId") 
            val selectedConsoleId = arguments?.getString("consoleId")
            
            if (selectedRoomId == null && selectedConsoleId == null) {
                Toast.makeText(context, "Error: No item selected!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (date.isEmpty() || time.isEmpty()) {
                 Toast.makeText(context, "Please select Date and Time", Toast.LENGTH_SHORT).show()
                 return@setOnClickListener
            }
            
            val totalCost = duration * hourlyRate
            val formattedCost = currencyFormat.format(totalCost)

            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Booking")
                .setMessage("Booking: $itemName\nDate: $date $time\nDuration: $duration hours\nBranch: $selectedBranch\nTotal: Rp $formattedCost")
                .setPositiveButton("Confirm") { _, _ ->
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
                        .setPopUpTo(R.id.navigation_catalog, true)
                        .build()
                     findNavController().navigate(R.id.navigation_profile, null, navOptions)
                } else {
                     Toast.makeText(context, "Booking Failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTotal() {
        val total = duration * hourlyRate
        val currencyFormat = java.text.NumberFormat.getIntegerInstance(java.util.Locale("id", "ID"))
        binding.tvTotalPrice.text = "Rp ${currencyFormat.format(total)}"
    }
}
