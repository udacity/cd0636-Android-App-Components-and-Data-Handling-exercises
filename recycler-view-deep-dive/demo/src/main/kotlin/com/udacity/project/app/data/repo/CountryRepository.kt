package com.udacity.project.app.data.repo

import com.udacity.project.app.data.api.CountriesService
import com.udacity.project.app.data.api.CountryApiService
import com.udacity.project.app.data.api.toEntities
import com.udacity.project.app.data.db.CountryDao
import com.udacity.project.app.data.db.toDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface CountryRepository {
    fun getCountries(): Flow<List<Country>>
    suspend fun refreshCountries(): Result<Unit>
}

class DefaultCountryRepository(
    private val countryDao: CountryDao,
    private val countryApiService: CountryApiService = CountriesService.api,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CountryRepository {
    override fun getCountries(): Flow<List<Country>> =
        countryDao.getAllCountriesWithDetails().map { list ->
            list.map { it.toDomainModel() }
        }.flowOn(dispatcher)

    override suspend fun refreshCountries(): Result<Unit> {
        return try {
            val responses = withContext(dispatcher) {
                countryApiService.getCountries()
            }
            val allEntities = responses.map { it.toEntities() }
            countryDao.refreshAllCountries(
                countries = allEntities.map { it.country },
                nativeNames = allEntities.flatMap { it.nativeNames },
                currencies = allEntities.flatMap { it.currencies }
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}