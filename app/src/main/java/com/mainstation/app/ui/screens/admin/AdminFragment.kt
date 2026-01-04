package com.mainstation.app.ui.screens.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Console
import com.mainstation.app.data.model.Room
import dagger.hilt.android.AndroidEntryPoint
import com.mainstation.app.databinding.FragmentAdminBinding
import com.mainstation.app.databinding.DialogAddConsoleBinding
import com.mainstation.app.databinding.DialogAddRoomBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminFragment : Fragment() {

    private val viewModel: AdminViewModel by viewModels()
    private lateinit var bookingAdapter: AdminBookingAdapter
    private lateinit var consoleAdapter: AdminConsoleAdapter
    private lateinit var roomAdapter: AdminRoomAdapter
    private var currentTab = "bookings" // bookings, consoles, rooms
    
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bookings Setup
        binding.rvAdminBookings.layoutManager = LinearLayoutManager(context)
        bookingAdapter = AdminBookingAdapter(
            bookings = emptyList(),
            onApprove = { id -> viewModel.approveBooking(id) },
            onReject = { id -> viewModel.rejectBooking(id) }
        )
        binding.rvAdminBookings.adapter = bookingAdapter

        // Consoles Setup
        binding.rvAdminConsoles.layoutManager = LinearLayoutManager(context)
        consoleAdapter = AdminConsoleAdapter(
            consoles = emptyList(),
            onEdit = { console -> showEditConsoleDialog(console) },
            onDelete = { id -> viewModel.deleteConsole(id) }
        )
        binding.rvAdminConsoles.adapter = consoleAdapter

        // Rooms Setup
        binding.rvAdminRooms.layoutManager = LinearLayoutManager(context)
        roomAdapter = AdminRoomAdapter(
            rooms = emptyList(),
            onEdit = { room -> showEditRoomDialog(room) },
            onDelete = { id -> viewModel.deleteRoom(id) }
        )
        binding.rvAdminRooms.adapter = roomAdapter

        // Observers
        viewLifecycleOwner.lifecycleScope.launch {
             viewModel.bookings.collect { bookingAdapter.updateData(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
             viewModel.consoles.collect { consoleAdapter.updateData(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
             viewModel.rooms.collect { roomAdapter.updateData(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                if (error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }

        fun updateTabs(selected: String) {
            currentTab = selected
            binding.rvAdminBookings.visibility = if (selected == "bookings") View.VISIBLE else View.GONE
            binding.rvAdminConsoles.visibility = if (selected == "consoles") View.VISIBLE else View.GONE
            binding.rvAdminRooms.visibility = if (selected == "rooms") View.VISIBLE else View.GONE
            
            // Fix: Hide FAB on bookings tab
            if (selected == "bookings") {
                binding.fabAdd.hide()
            } else {
                binding.fabAdd.show()
            }
            
            // Highlight (correctly using tint for MaterialButton)
            val purple = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#A855F7"))
            
            // Unselected = null (reverts to default TextButton style which is transparent)
            binding.tabBookings.backgroundTintList = if (selected == "bookings") purple else null
            binding.tabConsoles.backgroundTintList = if (selected == "consoles") purple else null
            binding.tabRooms.backgroundTintList = if (selected == "rooms") purple else null

            // Text Colors - update for visual consistency
            val white = android.graphics.Color.WHITE
            val gray = android.graphics.Color.parseColor("#CBD5E1") // slate-300
            
            binding.tabBookings.setTextColor(if (selected == "bookings") white else gray)
            binding.tabConsoles.setTextColor(if (selected == "consoles") white else gray)
            binding.tabRooms.setTextColor(if (selected == "rooms") white else gray)
        }

        binding.tabBookings.setOnClickListener { updateTabs("bookings") }
        binding.tabConsoles.setOnClickListener { updateTabs("consoles") }
        binding.tabRooms.setOnClickListener { updateTabs("rooms") }
        
        binding.fabAdd.setOnClickListener {
            if (currentTab == "consoles") showAddConsoleDialog()
            else if (currentTab == "rooms") showAddRoomDialog()
        }
        
        binding.btnSeed.setOnClickListener {
            viewModel.seedData()
            Toast.makeText(context, "Data Reset & Stock Updated!", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnLogout.setOnClickListener {
             findNavController().navigate(R.id.login_fragment)
        }
        
        // Initial state
        updateTabs("bookings")
    }
    
    private fun showAddConsoleDialog() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val sheetBinding = com.mainstation.app.databinding.DialogAddConsoleBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)
        
        sheetBinding.btnSave.setOnClickListener {
            val name = sheetBinding.etName.text.toString()
            val type = sheetBinding.etType.text.toString() 
            val price = sheetBinding.etPrice.text.toString().toDoubleOrNull()
            val stock = sheetBinding.etStock.text.toString().toIntOrNull() ?: 5 // Default 5
            
            if (name.isNotEmpty() && price != null && price >= 0 && stock >= 0) {
                viewModel.addConsole(name, type.ifEmpty { "PS5" }, price, stock)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid input: Name required, Price/Stock must be positive", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun showEditConsoleDialog(console: Console) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val sheetBinding = com.mainstation.app.databinding.DialogAddConsoleBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        sheetBinding.etName.setText(console.name)
        sheetBinding.etType.setText(console.type)
        sheetBinding.etPrice.setText(console.pricePerHour.toString())
        sheetBinding.etStock.setText(console.stock.toString())

        sheetBinding.btnSave.setOnClickListener {
            val name = sheetBinding.etName.text.toString()
            val type = sheetBinding.etType.text.toString()
            val price = sheetBinding.etPrice.text.toString().toDoubleOrNull()
            val stock = sheetBinding.etStock.text.toString().toIntOrNull() ?: console.stock

            if (name.isNotEmpty() && price != null && price >= 0 && stock >= 0) {
                val updatedConsole = console.copy(name = name, type = type, pricePerHour = price, stock = stock)
                viewModel.updateConsole(updatedConsole)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid input: Name required, Price/Stock must be positive", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
    
    private fun showAddRoomDialog() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val sheetBinding = com.mainstation.app.databinding.DialogAddRoomBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)
        
        sheetBinding.btnSave.setOnClickListener {
            val name = sheetBinding.etName.text.toString()
            val desc = sheetBinding.etDesc.text.toString()
            val cap = sheetBinding.etCapacity.text.toString().toIntOrNull() ?: 0
            val price = sheetBinding.etPrice.text.toString().toDoubleOrNull()
            
            if (name.isNotEmpty() && price != null && price >= 0 && cap >= 0) {
                viewModel.addRoom(name, cap, price, desc)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid input: Name required, Price/Capacity must be positive", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun showEditRoomDialog(room: Room) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val sheetBinding = com.mainstation.app.databinding.DialogAddRoomBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        sheetBinding.etName.setText(room.name)
        sheetBinding.etDesc.setText(room.description)
        sheetBinding.etCapacity.setText(room.capacity.toString())
        sheetBinding.etPrice.setText(room.pricePerHour.toString())

        sheetBinding.btnSave.setOnClickListener {
            val name = sheetBinding.etName.text.toString()
            val desc = sheetBinding.etDesc.text.toString()
            val cap = sheetBinding.etCapacity.text.toString().toIntOrNull() ?: 0
            val price = sheetBinding.etPrice.text.toString().toDoubleOrNull()

            if (name.isNotEmpty() && price != null && price >= 0 && cap >= 0) {
                val updatedRoom = room.copy(name = name, description = desc, capacity = cap, pricePerHour = price)
                viewModel.updateRoom(updatedRoom)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid input: Name required, Price/Capacity must be positive", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private val seedReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
            if (intent?.action == "com.mainstation.app.SEED_DATA") {
                viewModel.seedData()
                Toast.makeText(context, "Terminal Command: Database Reset & Seeded!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAllData()
        val filter = android.content.IntentFilter("com.mainstation.app.SEED_DATA")
        requireActivity().registerReceiver(seedReceiver, filter, android.content.Context.RECEIVER_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(seedReceiver)
    }
}
