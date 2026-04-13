package com.udacity.project.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.udacity.project.app.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

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
                is AuthState.Unauthenticated -> {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    binding.signUpButton.isEnabled = true
                }

                is AuthState.Authenticated -> {
                    binding.loadingProgressBar.visibility = View.GONE
                }
                is AuthState.Error -> {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    binding.signUpButton.isEnabled = true
                    Toast.makeText(
                        requireContext(), state.message, Toast.LENGTH_LONG
                    ).show()
                }
                AuthState.Loading -> {
                    binding.loadingProgressBar.visibility = View.VISIBLE
                    binding.loginButton.isEnabled = false
                    binding.signUpButton.isEnabled = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
