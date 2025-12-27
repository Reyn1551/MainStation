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
import kotlinx.coroutines.launch
import coil.load
import coil.transform.CircleCropTransformation

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_bookings)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        val tvName = view.findViewById<android.widget.TextView>(R.id.tv_user_name)
        val tvEmail = view.findViewById<android.widget.TextView>(R.id.tv_user_email)
        val ivAvatar = view.findViewById<android.widget.ImageView>(R.id.iv_user_avatar)

        viewLifecycleOwner.lifecycleScope.launch {
             viewModel.bookings.collect { bookings ->
                 recyclerView.adapter = BookingAdapter(bookings)
             }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fullName.collect { name ->
                tvName.text = name
            }
        }
        
        // Load User Email & Avatar
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user != null) {
            tvEmail.text = user.email
            
            // DiceBear API Keyless - Free
            val avatarUrl = "https://api.dicebear.com/9.x/avataaars/png?seed=${user.email}"
            ivAvatar.load(avatarUrl) {
                crossfade(true)
                transformations(CircleCropTransformation())
                placeholder(R.drawable.bg_button_gradient)
            }
        }
        
        val btnLocations = view.findViewById<android.view.View>(R.id.btn_locations)
        if (btnLocations != null) {
            btnLocations.setOnClickListener {
                findNavController().navigate(R.id.action_profile_to_locations)
            }
        }
        
        val btnLogout = view.findViewById<android.view.View>(R.id.btn_logout)
        if (btnLogout != null) {
            btnLogout.setOnClickListener {
                viewModel.logout()
                findNavController().navigate(R.id.login_fragment)
            }
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
