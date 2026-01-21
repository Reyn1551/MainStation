package com.mainstation.app.ui.screens.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mainstation.app.R
import dagger.hilt.android.AndroidEntryPoint
import com.mainstation.app.databinding.FragmentCatalogBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private val viewModel: CatalogViewModel by viewModels()
    private lateinit var adapter: ConsoleAdapter
    
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvConsoles.layoutManager = LinearLayoutManager(context)
        
        adapter = ConsoleAdapter(emptyList()) { console ->
            val bundle = Bundle().apply {
                putString("consoleId", console.id)
                putString("itemName", console.name)
                putFloat("hourlyRate", console.pricePerHour.toFloat()) // Gunakan putFloat
            }
            findNavController().navigate(R.id.action_catalog_to_booking, bundle)
        }
        binding.rvConsoles.adapter = adapter

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: View.NO_ID
            filterAndDisplay(viewModel.consoles.value, checkedId)
        }

        viewLifecycleOwner.lifecycleScope.launch {
             viewModel.consoles.collect { consoles ->
                 filterAndDisplay(consoles, binding.chipGroupFilter.checkedChipId)
             }
        }
    }
    
    private fun filterAndDisplay(consoles: List<com.mainstation.app.data.model.Console>, checkedId: Int) {
         val filtered = when (checkedId) {
             R.id.chip_ps5 -> consoles.filter { it.type.contains("PS5", ignoreCase = true) }
             R.id.chip_ps4 -> consoles.filter { it.type.contains("PS4", ignoreCase = true) }
             R.id.chip_xbox -> consoles.filter { it.type.contains("Xbox", ignoreCase = true) }
             else -> consoles
         }
         adapter.updateData(filtered)
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.loadConsoles()
    }
}
