package com.udacity.project.app.data.repo

import com.udacity.project.app.data.api.CountriesService
import com.udacity.project.app.data.api.CountryApiService
import com.udacity.project.app.data.api.toDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

interface CountryRepository {
    fun getCountries(): Flow<List<Country>>
    suspend fun refreshCountries(): Result<Unit>
    suspend fun syncIfNeeded(): Result<Unit>
}

class DefaultCountryRepository(
    private val countryApiService: CountryApiService = CountriesService.api,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CountryRepository {
    // Temp storage for countries to simulate a local cache
    private val _countriesCache = MutableStateFlow<List<Country>>(emptyList())

    override fun getCountries(): Flow<List<Country>> = flow {
        if (_countriesCache.value.isEmpty() || shouldRefresh()) {
            refreshCountries()
        }
        emitAll(_countriesCache)
    }.flowOn(dispatcher)

    override suspend fun refreshCountries(): Result<Unit> {
        return try {
            val responses = withContext(dispatcher) {
                countryApiService.getCountries()
            }
            //Todo: Save to database
            _countriesCache.value = responses.map { it.toDomainModel() }
            lastFetchTime = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncIfNeeded(): Result<Unit> {
        return if (shouldRefresh()) {
            refreshCountries()
        } else {
            Result.success(Unit)
        }
    }

    private fun shouldRefresh(): Boolean {
        return (System.currentTimeMillis() - lastFetchTime) > CACHE_TIMEOUT
    }

    private var lastFetchTime = 0L

    companion object {
        private const val CACHE_TIMEOUT = 5 * 60 * 1000L  // 5 minutes
    }
}
