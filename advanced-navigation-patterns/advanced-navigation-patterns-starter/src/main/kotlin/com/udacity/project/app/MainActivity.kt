package com.udacity.project.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the toolbar as the action bar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // TODO 3.1: Get NavHostFragment and NavController
        // Find the NavHostFragment by ID (R.id.nav_host_fragment)
        // Get its NavController

        // TODO 3.2: Set up BottomNavigationView with NavController
        // Find the BottomNavigationView by ID (R.id.bottom_nav)
        // Call setupWithNavController() on it with the NavController
        // This automatically handles tab switching and back stack management

        // TODO 3.3: Configure AppBar with top-level destinations
        // Create AppBarConfiguration with setOf(R.id.nav_all_tasks, R.id.nav_favorites, R.id.nav_completed)
        // Call setupActionBarWithNavController() with NavController and AppBarConfiguration
    }

    override fun onSupportNavigateUp(): Boolean {
        // TODO 3.4: Implement up navigation
        // Get NavController and call navigateUp(), return result || super.onSupportNavigateUp()
        return super.onSupportNavigateUp()
    }
}