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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private val viewModel: CatalogViewModel by viewModels()
    private lateinit var adapter: ConsoleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_consoles)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        
        adapter = ConsoleAdapter(emptyList()) { console ->
            val bundle = Bundle().apply {
                putString("consoleId", console.id)
                putString("itemName", console.name)
                putDouble("hourlyRate", console.pricePerHour)
            }
            findNavController().navigate(R.id.action_catalog_to_booking, bundle)
        }
        recyclerView.adapter = adapter
        
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
