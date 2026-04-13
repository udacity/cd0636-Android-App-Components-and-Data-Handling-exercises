package com.udacity.project.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.udacity.project.app.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var securePrefs: SecurePreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        securePrefs = SecurePreferences(requireContext())

        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            authViewModel.login(email, password)
        }

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            authViewModel.signUp(email, password)
        }
    }

    private fun observeAuthState() {
        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Authenticated -> {
                    // Save email and token to secure storage
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            securePrefs.saveUserEmail(state.email)
                            state.token?.let { token ->
                                securePrefs.saveAuthToken(token)
                            }
                        } catch (e: Exception) {
                            // Continue even if secure storage fails
                        }
                    }

                    // Navigation handled by MainActivity
                    binding.loadingProgressBar.visibility = View.GONE
                }
                is AuthState.Loading -> {
                    binding.loadingProgressBar.visibility = View.VISIBLE
                    binding.loginButton.isEnabled = false
                    binding.signUpButton.isEnabled = false
                }
                is AuthState.Unauthenticated -> {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    binding.signUpButton.isEnabled = true
                }
                is AuthState.Error -> {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    binding.signUpButton.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    authViewModel.resetErrorState()
                }
                is AuthState.SessionExpired -> {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    binding.signUpButton.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        "Session expired. Please log in again.",
                        Toast.LENGTH_LONG
                    ).show()
                    authViewModel.resetErrorState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}