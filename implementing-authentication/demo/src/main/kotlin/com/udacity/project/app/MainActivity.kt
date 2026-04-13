package com.udacity.project.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.udacity.project.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupToolbar()
        setupNavigation()
        observeAuthState()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.loginFragment, R.id.movieListFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    // TODO Step 3: Uncomment observeAuthState() and menu methods
     private fun observeAuthState() {
         authViewModel.authState.observe(this) { state ->
             val navHostFragment = supportFragmentManager
                 .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
             val navController = navHostFragment.navController

             when (state) {
                 is AuthState.Authenticated -> {
                     if (navController.currentDestination?.id == R.id.loginFragment) {
                         navController.navigate(R.id.action_loginFragment_to_movieListFragment)
                     }
                     invalidateOptionsMenu()
                 }
                 is AuthState.Unauthenticated -> {
                     if (navController.currentDestination?.id != R.id.loginFragment) {
                         val navOptions = androidx.navigation.NavOptions.Builder()
                             .setPopUpTo(R.id.movieListFragment, inclusive = true)
                             .build()
                         navController.navigate(R.id.loginFragment, null, navOptions)
                     }
                     invalidateOptionsMenu()
                 }
                 else -> {}
             }
         }
     }

     override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
         menuInflater.inflate(R.menu.main_menu, menu)
         val isAuthenticated = authViewModel.authState.value is AuthState.Authenticated
         menu?.findItem(R.id.action_logout)?.isVisible = isAuthenticated
         return true
     }

     override fun onPrepareOptionsMenu(menu: android.view.Menu?): Boolean {
         val isAuthenticated = authViewModel.authState.value is AuthState.Authenticated
         menu?.findItem(R.id.action_logout)?.isVisible = isAuthenticated
         return super.onPrepareOptionsMenu(menu)
     }

     override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
         return when (item.itemId) {
             R.id.action_logout -> {
                 authViewModel.logout()
                 true
             }
             else -> super.onOptionsItemSelected(item)
         }
     }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
