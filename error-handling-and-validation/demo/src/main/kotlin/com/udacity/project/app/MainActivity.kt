package com.udacity.project.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class  MainActivity : AppCompatActivity() {

    private val viewModel: SignUpViewModel by viewModels()

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var signUpButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorIcon: ImageView
    private lateinit var errorMessageTextView: TextView
    private lateinit var errorSubtitleTextView: TextView
    private lateinit var retryButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupValidation()
        setupClickListeners()
        collectFlows()
    }

    private fun initializeViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout)
        signUpButton = findViewById(R.id.signUpButton)
        progressBar = findViewById(R.id.progressBar)
        errorContainer = findViewById(R.id.errorContainer)
        errorIcon = findViewById(R.id.errorIcon)
        errorMessageTextView = findViewById(R.id.errorMessageTextView)
        errorSubtitleTextView = findViewById(R.id.errorSubtitleTextView)
        retryButton = findViewById(R.id.retryButton)
    }

    private fun setupValidation() {
        emailInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.validateEmail(s.toString().trim())
            }
        })

        passwordInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.validatePassword(s.toString())
            }
        })

        confirmPasswordInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.validateConfirmPassword(s.toString())
            }
        })
    }

    private fun setupClickListeners() {
        signUpButton.setOnClickListener {
            viewModel.signUp()
        }

        retryButton.setOnClickListener {
            viewModel.signUp()
        }
    }

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

                launch {
                    viewModel.signUpState.collect { state ->
                        when (state) {
                            is SignUpResult.Idle -> { /* default state */ }
                            is SignUpResult.Loading -> {
                                progressBar.visibility = View.VISIBLE
                                errorContainer.visibility = View.GONE
                                signUpButton.isEnabled = false
                            }
                            is SignUpResult.Success -> {
                                progressBar.visibility = View.GONE
                                signUpButton.isEnabled = true
                                Snackbar.make(
                                    findViewById(R.id.main),
                                    "Account created successfully!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            is SignUpResult.Error -> {
                                progressBar.visibility = View.GONE
                                signUpButton.isEnabled = true
                                showErrorState(state.error)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showErrorState(error: SignUpError) {
        errorContainer.visibility = View.VISIBLE
        errorIcon.setColorFilter(ContextCompat.getColor(this, R.color.error_red))
        errorMessageTextView.text = error.message

        errorSubtitleTextView.text = when (error) {
            is SignUpError.NoInternet ->
                "Check your Wi-Fi or mobile data and try again."
            is SignUpError.Timeout ->
                "The server is taking too long to respond."
            is SignUpError.ServerError ->
                "Our servers are having trouble. Try again later."
            is SignUpError.EmailAlreadyExists ->
                "Try signing in instead, or use a different email."
            is SignUpError.WeakPassword ->
                "Try adding more characters, numbers, or symbols."
            is SignUpError.Unknown ->
                "An unexpected error occurred. Please try again."
        }
    }
}
