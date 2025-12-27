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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = view.findViewById<EditText>(R.id.et_email)
        val passwordInput = view.findViewById<EditText>(R.id.et_password)
        val loginButton = view.findViewById<Button>(R.id.btn_login)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
        
        val tvSignUp = view.findViewById<android.widget.TextView>(R.id.tv_signup)
        
        // Make "Sign Up" colored
        val spannable = android.text.SpannableString("Don't have an account? Sign Up")
        val colorSpan = android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#EC4899")) // Pink-500
        spannable.setSpan(colorSpan, 23, 30, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvSignUp.text = spannable
        
        tvSignUp.setOnClickListener {
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
        val view = layoutInflater.inflate(R.layout.bottom_sheet_register, null)
        dialog.setContentView(view)
        
        // Transparent background for rounded corners
        (view.parent as? View)?.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        val nameInput = view.findViewById<EditText>(R.id.et_reg_name)
        val emailInput = view.findViewById<EditText>(R.id.et_reg_email)
        val passwordInput = view.findViewById<EditText>(R.id.et_reg_password)
        val btnRegister = view.findViewById<Button>(R.id.btn_register)
        
        btnRegister.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            
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
