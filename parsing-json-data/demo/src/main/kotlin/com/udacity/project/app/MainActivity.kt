package com.udacity.project.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: CountryViewModel

    private lateinit var countryAdapter: CountryAdapter
    private lateinit var countriesRecyclerView: RecyclerView
    private lateinit var syncButton: Button
    private lateinit var totalCountriesTextView: TextView
    private lateinit var syncStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[CountryViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeSyncState()
        observeCountries()
    }

    private fun initializeViews() {
        countriesRecyclerView = findViewById(R.id.countriesRecyclerView)
        syncButton = findViewById(R.id.syncButton)
        totalCountriesTextView = findViewById(R.id.totalCountriesTextView)
        syncStatusTextView = findViewById(R.id.syncStatusTextView)
    }

    private fun setupRecyclerView() {
        countryAdapter = CountryAdapter()
        countriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = countryAdapter
        }
    }

    private fun setupClickListeners() {
        syncButton.setOnClickListener {
            viewModel.syncCountries()
        }
    }

    private fun observeSyncState() {
        lifecycleScope.launch {
            viewModel.syncState.collect { state ->
                when (state) {
                    is SyncState.Idle -> {
                        syncStatusTextView.visibility = View.GONE
                        syncButton.isEnabled = true
                        syncButton.text = "Fetch Countries"
                    }
                    is SyncState.Loading -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Fetching from REST Countries API..."
                        syncButton.isEnabled = false
                        syncButton.text = "Fetching..."
                    }
                    is SyncState.Success -> {
                        val count = viewModel.countries.value.size
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Loaded $count countries from API"
                        syncButton.isEnabled = true
                        syncButton.text = "Fetch Countries"

                        syncStatusTextView.postDelayed({
                            syncStatusTextView.visibility = View.GONE
                        }, 3000)

                        Toast.makeText(
                            this@MainActivity,
                            "Loaded $count countries",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is SyncState.Error -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Failed: ${state.message}"
                        syncButton.isEnabled = true
                        syncButton.text = "Retry"

                        Toast.makeText(
                            this@MainActivity,
                            "Failed: ${state.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun observeCountries() {
        lifecycleScope.launch {
            viewModel.countries.collect { countries ->
                countryAdapter.updateCountries(countries)
                totalCountriesTextView.text = "Total: ${countries.size} countries"
            }
        }
    }
}
