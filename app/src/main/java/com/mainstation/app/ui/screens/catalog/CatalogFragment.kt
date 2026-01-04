package com.mainstation.app.ui.screens.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        binding.rvConsoles.layoutManager = GridLayoutManager(context, 2)
        
        adapter = ConsoleAdapter(emptyList()) { console ->
            val bundle = Bundle().apply {
                putString("consoleId", console.id)
                putString("itemName", console.name)
                putDouble("hourlyRate", console.pricePerHour)
            }
            findNavController().navigate(R.id.action_catalog_to_booking, bundle)
        }
        binding.rvConsoles.adapter = adapter
        
        viewLifecycleOwner.lifecycleScope.launch {
             viewModel.consoles.collect { consoles ->
                 adapter.updateData(consoles)
             }
        }
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.loadConsoles()
    }
}
