package com.udacity.project.app

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.udacity.project.app.ui.CountryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryViewModelTest {

    @Test
    fun syncCountries_success_updatesState() = runTest {
        val fakeRepo = FakeCountryRepository()
        val viewModel = CountryViewModel(fakeRepo)

        viewModel.countries.test {
            awaitItem()
            val countries = awaitItem()
            assertThat(countries).hasSize(2)
            cancelAndIgnoreRemainingEvents()
        }

        assertThat(viewModel.syncState.value).contains("Loaded")
    }


    @Test
    fun syncCountries_failure_showsError() = runTest {
        val fakeRepo = FakeCountryRepository(shouldFail = true)
        val viewModel = CountryViewModel(fakeRepo)

        viewModel.syncState.test {
            viewModel.syncCountries()
            awaitItem()
            awaitItem()
            val errorState = awaitItem()
            assertThat(errorState).contains("Error")
            cancelAndIgnoreRemainingEvents()
        }
    }
}