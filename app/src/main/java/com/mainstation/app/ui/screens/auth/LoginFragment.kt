package com.mainstation.app.ui.screens.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mainstation.app.R
import dagger.hilt.android.AndroidEntryPoint
import com.mainstation.app.databinding.FragmentLoginBinding
import com.mainstation.app.databinding.BottomSheetRegisterBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (email.matches(emailPattern.toRegex())) {
                    viewModel.login(email, password)
                } else {
                    Toast.makeText(context, "Invalid Email Format", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Make "Sign Up" colored
        val spannable = android.text.SpannableString("Don't have an account? Sign Up")
        val colorSpan = android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#EC4899")) // Pink-500
        spannable.setSpan(colorSpan, 23, 30, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvSignup.text = spannable
        
        binding.tvSignup.setOnClickListener {
            showRegisterBottomSheet()
        }

        viewLifecycleOwner.lifecycleScope.launch {
             viewModel.uiState.collect { state ->
                 if (state.isLoggedIn && state.user != null) {
                     // Check if we are still on login fragment to avoid crashes or loops
                     if (findNavController().currentDestination?.id == R.id.login_fragment) {
                         if (state.user.role == "ADMIN") {
                             findNavController().navigate(R.id.action_login_to_admin)
                         } else {
                             findNavController().navigate(R.id.action_login_to_catalog)
                         }
                     }
                 }
                 if (state.error != null) {
                    Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                    viewModel.clearError() 
                 }
             }
        }
    }
    
    private fun showRegisterBottomSheet() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val sheetBinding = com.mainstation.app.databinding.BottomSheetRegisterBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)
        
        (sheetBinding.root.parent as? View)?.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        sheetBinding.btnRegister.setOnClickListener {
            val name = sheetBinding.etRegName.text.toString()
            val email = sheetBinding.etRegEmail.text.toString()
            val password = sheetBinding.etRegPassword.text.toString()
            
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Call Register in ViewModel
                // For now, reuse AuthViewModel but we need to add register function there OR use repository directly if simple
                // Let's assume we add register to ViewModel next.
                viewModel.register(name, email, password)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        
        dialog.show()
    }
}
