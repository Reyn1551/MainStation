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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminFragment : Fragment() {

    private val viewModel: AdminViewModel by viewModels()
    private lateinit var bookingAdapter: AdminBookingAdapter
    private lateinit var consoleAdapter: AdminConsoleAdapter
    private lateinit var roomAdapter: AdminRoomAdapter
    private var currentTab = "bookings" // bookings, consoles, rooms

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bookings Setup
        val rvBookings = view.findViewById<RecyclerView>(R.id.rv_admin_bookings)
        rvBookings.layoutManager = LinearLayoutManager(context)
        bookingAdapter = AdminBookingAdapter(
            bookings = emptyList(),
            onApprove = { id -> viewModel.approveBooking(id) },
            onReject = { id -> viewModel.rejectBooking(id) }
        )
        rvBookings.adapter = bookingAdapter

        // Consoles Setup
        val rvConsoles = view.findViewById<RecyclerView>(R.id.rv_admin_consoles)
        rvConsoles.layoutManager = LinearLayoutManager(context)
        consoleAdapter = AdminConsoleAdapter(
            consoles = emptyList(),
            onEdit = { console -> showEditConsoleDialog(console) },
            onDelete = { id -> viewModel.deleteConsole(id) }
        )
        rvConsoles.adapter = consoleAdapter

        // Rooms Setup
        val rvRooms = view.findViewById<RecyclerView>(R.id.rv_admin_rooms)
        rvRooms.layoutManager = LinearLayoutManager(context)
        roomAdapter = AdminRoomAdapter(
            rooms = emptyList(),
            onEdit = { room -> showEditRoomDialog(room) },
            onDelete = { id -> viewModel.deleteRoom(id) }
        )
        rvRooms.adapter = roomAdapter

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

        // UI Controls
        val fabAdd = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add)
        val tabBookings = view.findViewById<View>(R.id.tab_bookings)
        val tabConsoles = view.findViewById<View>(R.id.tab_consoles)
        val tabRooms = view.findViewById<View>(R.id.tab_rooms)

        fun updateTabs(selected: String) {
            currentTab = selected
            rvBookings.visibility = if (selected == "bookings") View.VISIBLE else View.GONE
            rvConsoles.visibility = if (selected == "consoles") View.VISIBLE else View.GONE
            rvRooms.visibility = if (selected == "rooms") View.VISIBLE else View.GONE
            
            // Highlight (correctly using tint for MaterialButton)
            val purple = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#A855F7"))
            
            // Unselected = null (reverts to default TextButton style which is transparent)
            tabBookings.backgroundTintList = if (selected == "bookings") purple else null
            tabConsoles.backgroundTintList = if (selected == "consoles") purple else null
            tabRooms.backgroundTintList = if (selected == "rooms") purple else null

            // Text Colors - update for visual consistency
            val white = android.graphics.Color.WHITE
            val gray = android.graphics.Color.parseColor("#CBD5E1") // slate-300
            
            (tabBookings as android.widget.Button).setTextColor(if (selected == "bookings") white else gray)
            (tabConsoles as android.widget.Button).setTextColor(if (selected == "consoles") white else gray)
            (tabRooms as android.widget.Button).setTextColor(if (selected == "rooms") white else gray)
        }

        tabBookings.setOnClickListener { updateTabs("bookings") }
        tabConsoles.setOnClickListener { updateTabs("consoles") }
        tabRooms.setOnClickListener { updateTabs("rooms") }
        
        fabAdd.setOnClickListener {
            if (currentTab == "consoles") showAddConsoleDialog()
            else if (currentTab == "rooms") showAddRoomDialog()
        }
        
        view.findViewById<View>(R.id.btn_seed).setOnClickListener {
            viewModel.seedData()
            Toast.makeText(context, "Data Reset & Stock Updated!", Toast.LENGTH_SHORT).show()
        }
        
        view.findViewById<View>(R.id.btn_logout).setOnClickListener {
             findNavController().navigate(R.id.login_fragment)
        }
        
        // Initial state
        updateTabs("bookings")
    }
    
    private fun showAddConsoleDialog() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_console, null)
        dialog.setContentView(view)
        
        val etName = view.findViewById<android.widget.EditText>(R.id.et_name)
        val etType = view.findViewById<android.widget.EditText>(R.id.et_type)
        val etPrice = view.findViewById<android.widget.EditText>(R.id.et_price)
        val etStock = view.findViewById<android.widget.EditText>(R.id.et_stock)
        
        view.findViewById<View>(R.id.btn_save).setOnClickListener {
            val name = etName.text.toString()
            val type = etType.text.toString() 
            val price = etPrice.text.toString().toDoubleOrNull()
            val stock = etStock.text.toString().toIntOrNull() ?: 5 // Default 5
            
            if (name.isNotEmpty() && price != null) {
                viewModel.addConsole(name, type.ifEmpty { "PS5" }, price, stock)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun showEditConsoleDialog(console: Console) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_console, null)
        dialog.setContentView(view)

        val etName = view.findViewById<android.widget.EditText>(R.id.et_name)
        val etType = view.findViewById<android.widget.EditText>(R.id.et_type)
        val etPrice = view.findViewById<android.widget.EditText>(R.id.et_price)
        val etStock = view.findViewById<android.widget.EditText>(R.id.et_stock)

        etName.setText(console.name)
        etType.setText(console.type)
        etPrice.setText(console.pricePerHour.toString())
        etStock.setText(console.stock.toString())

        view.findViewById<View>(R.id.btn_save).setOnClickListener {
            val name = etName.text.toString()
            val type = etType.text.toString()
            val price = etPrice.text.toString().toDoubleOrNull()
            val stock = etStock.text.toString().toIntOrNull() ?: console.stock

            if (name.isNotEmpty() && price != null) {
                val updatedConsole = console.copy(name = name, type = type, pricePerHour = price, stock = stock)
                viewModel.updateConsole(updatedConsole)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
    
    private fun showAddRoomDialog() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_room, null)
        dialog.setContentView(view)
        
        val etName = view.findViewById<android.widget.EditText>(R.id.et_name)
        val etDesc = view.findViewById<android.widget.EditText>(R.id.et_desc)
        val etCap = view.findViewById<android.widget.EditText>(R.id.et_capacity)
        val etPrice = view.findViewById<android.widget.EditText>(R.id.et_price)
        
        view.findViewById<View>(R.id.btn_save).setOnClickListener {
            val name = etName.text.toString()
            val desc = etDesc.text.toString()
            val cap = etCap.text.toString().toIntOrNull() ?: 0
            val price = etPrice.text.toString().toDoubleOrNull()
            
            if (name.isNotEmpty() && price != null) {
                viewModel.addRoom(name, cap, price, desc)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun showEditRoomDialog(room: Room) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_room, null)
        dialog.setContentView(view)

        val etName = view.findViewById<android.widget.EditText>(R.id.et_name)
        val etDesc = view.findViewById<android.widget.EditText>(R.id.et_desc)
        val etCap = view.findViewById<android.widget.EditText>(R.id.et_capacity)
        val etPrice = view.findViewById<android.widget.EditText>(R.id.et_price)

        etName.setText(room.name)
        etDesc.setText(room.description)
        etCap.setText(room.capacity.toString())
        etPrice.setText(room.pricePerHour.toString())

        view.findViewById<View>(R.id.btn_save).setOnClickListener {
            val name = etName.text.toString()
            val desc = etDesc.text.toString()
            val cap = etCap.text.toString().toIntOrNull() ?: 0
            val price = etPrice.text.toString().toDoubleOrNull()

            if (name.isNotEmpty() && price != null) {
                val updatedRoom = room.copy(name = name, description = desc, capacity = cap, pricePerHour = price)
                viewModel.updateRoom(updatedRoom)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
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
