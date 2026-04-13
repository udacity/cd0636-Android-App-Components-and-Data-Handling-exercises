package com.udacity.project.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.launch

/**
 * Fragment for user authentication.
 * Provides email/password login and sign-up options.
 */
class LoginFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var securePreferences: SecurePreferences

    // UI Components
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signUpButton: MaterialButton
    private lateinit var errorTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        securePreferences = SecurePreferences(requireContext())

        initializeViews(view)
        setupClickListeners()
        observeAuthState()
    }

    private fun initializeViews(view: View) {
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)
        signUpButton = view.findViewById(R.id.signUpButton)
        errorTextView = view.findViewById(R.id.errorTextView)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            authViewModel.login(email, password)
        }

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            authViewModel.signUp(email, password)
        }
    }

    private fun observeAuthState() {
        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Authenticated -> {
                    // TODO 3.1 (Usage): Save email and token to secure storage
                    // After implementing SecurePreferences (TODO 3.1.1-3.1.7), use it here to save user data
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            securePreferences.saveUserEmail(state.email)
                            // TODO 3.2 (Usage): Save the auth token if present
                            // After implementing token retrieval in AuthViewModel (TODO 3.2.1-3.2.4),
                            // the token will be available in state.token and saved here
                            state.token?.let { token ->
                                securePreferences.saveAuthToken(token)
                            }
                        } catch (e: Exception) {
                            // Continue even if secure storage fails
                        }
                    }
                    hideLoading()
                    hideError()
                }
                is AuthState.Unauthenticated -> {
                    hideLoading()
                    hideError()
                }
                is AuthState.Loading -> {
                    showLoading()
                    hideError()
                }
                is AuthState.Error -> {
                    hideLoading()
                    showError(state.message)
                }
                is AuthState.SessionExpired -> {
                    hideLoading()
                    showError("Your session has expired. Please log in again.")
                }
            }
        }
    }

    private fun showLoading() {
        loadingProgressBar.visibility = View.VISIBLE
        loginButton.isEnabled = false
        signUpButton.isEnabled = false
    }

    private fun hideLoading() {
        loadingProgressBar.visibility = View.GONE
        loginButton.isEnabled = true
        signUpButton.isEnabled = true
    }

    private fun showError(message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }

    private fun hideError() {
        errorTextView.visibility = View.GONE
    }
}