package com.udacity.project.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.udacity.project.app.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var securePrefs: SecurePreferences
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        securePrefs = SecurePreferences(this)

        setupToolbar()
        setupNavigation()
        observeAuthState()
        authViewModel.checkAuthStatus()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Configure which destinations should not show back button
        // Task list is a top-level destination (no back button shown)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.taskListFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun observeAuthState() {
        authViewModel.authState.observe(this) { state ->
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            when (state) {
                is AuthState.Authenticated -> {
                    if (navController.currentDestination?.id == R.id.loginFragment) {
                        navController.navigate(R.id.action_loginFragment_to_taskListFragment)
                    }
                    invalidateOptionsMenu()
                }
                is AuthState.Unauthenticated, is AuthState.SessionExpired -> {
                    if (navController.currentDestination?.id != R.id.loginFragment) {
                        // Clear backstack and navigate to login as new root
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.taskListFragment, inclusive = true)
                            .build()
                        navController.navigate(R.id.loginFragment, null, navOptions)
                    }
                    invalidateOptionsMenu()
                }
                else -> {
                    // Loading or Error states handled by LoginFragment
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        updateMenuVisibility(menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        updateMenuVisibility(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    private fun updateMenuVisibility(menu: Menu?) {
        val isAuthenticated = authViewModel.authState.value is AuthState.Authenticated
        menu?.findItem(R.id.action_logout)?.isVisible = isAuthenticated
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                lifecycleScope.launch {
                    try {
                        securePrefs.clear()
                    } catch (e: Exception) {
                        // Continue with logout even if clearing preferences fails
                    }
                    authViewModel.logout()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}