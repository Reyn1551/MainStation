package com.mainstation.app.ui.screens.profile

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
import dagger.hilt.android.AndroidEntryPoint
import com.mainstation.app.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import coil.load
import coil.transform.CircleCropTransformation

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBookings.layoutManager = LinearLayoutManager(context)
        
        viewLifecycleOwner.lifecycleScope.launch {
             viewModel.bookings.collect { bookings ->
                 binding.rvBookings.adapter = BookingAdapter(bookings)
             }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fullName.collect { name ->
                binding.tvUserName.text = name
            }
        }
        
        // Load User Email & Avatar
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user != null) {
            binding.tvUserEmail.text = user.email
            
            // DiceBear API Keyless - Free
            val avatarUrl = "https://api.dicebear.com/9.x/avataaars/png?seed=${user.email}"
            binding.ivUserAvatar.load(avatarUrl) {
                crossfade(true)
                transformations(CircleCropTransformation())
                placeholder(R.drawable.bg_button_gradient)
            }
        }
        
        binding.btnLocations.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_locations)
        }
        
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.login_fragment)
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.seedResult.collect { msg ->
                if (msg != null) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.clearSeedMessage()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProfileData()
    }
}
