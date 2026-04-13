package com.udacity.project.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.udacity.project.app.databinding.ActivityMainBinding

/**
 * Main activity that hosts authentication-aware navigation.
 * Manages navigation between login and task screens based on authentication state.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNavigation()
        observeAuthState()

        // Check authentication status on app start (auto-login)
        authViewModel.checkAuthStatus()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Configure which destinations should not show back button
        // Task list is a top-level destination (no back button shown)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.taskListFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Observes authentication state and navigates accordingly.
     * - Authenticated: Navigate to task list
     * - Unauthenticated: Navigate to login
     * - SessionExpired: Show dialog and navigate to login
     */
    private fun observeAuthState() {
        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Authenticated -> {
                    // User is authenticated, navigate to task list if on login screen
                    val currentDestination = navController.currentDestination?.id
                    if (currentDestination == R.id.loginFragment) {
                        try {
                            navController.navigate(R.id.action_loginFragment_to_taskListFragment)
                        } catch (e: Exception) {
                            // Handle navigation exception (e.g., action not found)
                            e.printStackTrace()
                        }
                    }
                }
                is AuthState.Unauthenticated -> {
                    // User is not authenticated, navigate to login if not already there
                    val currentDestination = navController.currentDestination?.id
                    if (currentDestination != R.id.loginFragment && currentDestination == R.id.taskListFragment) {
                        try {
                            // Clear backstack when navigating to login on logout
                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(navController.graph.startDestinationId, inclusive = true)
                                .build()
                            navController.navigate(R.id.action_taskListFragment_to_loginFragment, null, navOptions)
                        } catch (e: Exception) {
                            // Handle navigation exception
                            e.printStackTrace()
                        }
                    }
                }
                is AuthState.SessionExpired -> {
                    // Session expired, show message and navigate to login
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Session Expired")
                        .setMessage("Your session has expired. Please log in again.")
                        .setPositiveButton("OK") { _, _ ->
                            val currentDestination = navController.currentDestination?.id
                            if (currentDestination != R.id.loginFragment && currentDestination == R.id.taskListFragment) {
                                try {
                                    // Clear backstack when navigating to login after session expiration
                                    val navOptions = NavOptions.Builder()
                                        .setPopUpTo(navController.graph.startDestinationId, inclusive = true)
                                        .build()
                                    navController.navigate(R.id.action_taskListFragment_to_loginFragment, null, navOptions)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        .setCancelable(false)
                        .show()
                }
                is AuthState.Loading, is AuthState.Error -> {
                    // Loading and error states are handled in LoginFragment
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}