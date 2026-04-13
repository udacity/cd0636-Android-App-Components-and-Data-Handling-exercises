package com.udacity.project.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: SignUpViewModel by viewModels()

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var signUpButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupTextWatchers()
        collectFlows()
        setupClickListeners()
        observeSignUpState()
    }

    private fun initializeViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        signUpButton = findViewById(R.id.signUpButton)
    }

    // Part 2.1: Add TextWatchers for real-time validation
    private fun setupTextWatchers() {
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.validateEmail(s.toString())
            }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.validatePassword(s.toString())
            }
        })

        confirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.validateConfirmPassword(s.toString())
            }
        })
    }

    // Part 2.2: Collect StateFlows to display errors and control the button
    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.emailError.collect { error ->
                        emailInputLayout.error = error
                    }
                }
                launch {
                    viewModel.passwordError.collect { error ->
                        passwordInputLayout.error = error
                    }
                }
                launch {
                    viewModel.confirmPasswordError.collect { error ->
                        confirmPasswordInputLayout.error = error
                    }
                }
                launch {
                    viewModel.isFormValid.collect { isValid ->
                        signUpButton.isEnabled = isValid
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        signUpButton.setOnClickListener {
            viewModel.signUp()
        }
    }

    // Pre-wired: observes signUpState to show loading/success feedback
    private fun observeSignUpState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signUpState.collect { state ->
                    when (state) {
                        is SignUpViewModel.SignUpState.Loading -> {
                            signUpButton.isEnabled = false
                            signUpButton.text = "Signing up..."
                        }

                        is SignUpViewModel.SignUpState.Success -> {
                            signUpButton.text = getString(R.string.sign_up)
                            Snackbar.make(
                                findViewById(android.R.id.content),
                                "Sign up successful!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is SignUpViewModel.SignUpState.Idle -> {
                            signUpButton.text = getString(R.string.sign_up)
                        }
                    }
                }
            }
        }
    }
}
