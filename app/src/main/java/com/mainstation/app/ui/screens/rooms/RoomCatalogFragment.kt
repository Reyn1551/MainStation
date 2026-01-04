package com.mainstation.app.ui.screens.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Room
import dagger.hilt.android.AndroidEntryPoint
import com.mainstation.app.databinding.FragmentRoomsBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint

class RoomCatalogFragment : Fragment() {

    private val viewModel: RoomViewModel by viewModels()
    
    private var _binding: FragmentRoomsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRooms.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.rooms.collect { rooms ->
                binding.rvRooms.adapter = RoomAdapter(rooms) { room ->
                    val bundle = Bundle().apply {
                        putString("roomId", room.id)
                        putString("itemName", room.name)
                        putDouble("hourlyRate", room.pricePerHour)
                    }
                    findNavController().navigate(R.id.action_rooms_to_booking, bundle)
                }
            }
        }
    }
}
