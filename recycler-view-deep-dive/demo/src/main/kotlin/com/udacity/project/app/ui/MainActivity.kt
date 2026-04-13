package com.udacity.project.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project.app.R
import com.udacity.project.app.data.api.CountriesService
import com.udacity.project.app.data.db.CountryDatabase
import com.udacity.project.app.data.repo.DefaultCountryRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: CountryViewModel by viewModels {
        CountriesService.init(application)
        val database = CountryDatabase.getDatabase(application)
        val repository = DefaultCountryRepository(database.countryDao())
        CountryViewModelFactory(repository)
    }

    private lateinit var countryAdapter: CountryAdapter
    private lateinit var countriesRecyclerView: RecyclerView
    private lateinit var syncButton: Button
    private lateinit var totalCountriesTextView: TextView
    private lateinit var favoriteCountriesTextView: TextView
    private lateinit var syncStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        collectFlows()
    }

    private fun initializeViews() {
        countriesRecyclerView = findViewById(R.id.countriesRecyclerView)
        syncButton = findViewById(R.id.syncButton)
        totalCountriesTextView = findViewById(R.id.totalCountriesTextView)
        favoriteCountriesTextView = findViewById(R.id.favoriteCountriesTextView)
        syncStatusTextView = findViewById(R.id.syncStatusTextView)
    }

    private fun setupRecyclerView() {
        countryAdapter = CountryAdapter(
            onFavoriteToggled = { countryName ->
                viewModel.toggleFavorite(countryName)
            }
        )
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
                        countryAdapter.submitList(countries)
                    }
                }
                launch {
                    viewModel.totalCount.collect { count ->
                        totalCountriesTextView.text = "Total: $count"
                    }
                }

                launch {
                    viewModel.favoriteCount.collect { count ->
                        favoriteCountriesTextView.text = "Favorites: $count"
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
