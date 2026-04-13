package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

        CountriesService.init(this)
        viewModel = ViewModelProvider(this)[CountryViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        collectFlows()
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

    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.countries.collect { countries ->
                        countryAdapter.updateCountries(countries)
                    }
                }

                launch {
                    viewModel.totalCount.collect { count ->
                        totalCountriesTextView.text = "Total: $count countries"
                    }
                }

                launch {
                    viewModel.syncState.collect { status ->
                        syncStatusTextView.text = status
                    }
                }
            }
        }
    }
}
