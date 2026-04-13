package com.udacity.project.app.data.repo

import com.udacity.project.app.data.api.CountryApiService
import com.udacity.project.app.data.api.toEntities
import com.udacity.project.app.data.db.CountryDao
import com.udacity.project.app.data.db.toDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface CountryRepository {
    fun getCountries(): Flow<List<Country>>
    suspend fun refreshCountries(): Result<Unit>
}

class DefaultCountryRepository(
    private val countryDao: CountryDao,
    private val countryApiService: CountryApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CountryRepository {

    override fun getCountries(): Flow<List<Country>> {
        return countryDao.getAllCountriesWithDetails().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun refreshCountries(): Result<Unit> {
        return try {
            val responses = withContext(dispatcher) {
                countryApiService.getCountries()
            }
            val allEntities = responses.map { it.toEntities() }
            countryDao.deleteAllCountries()
            countryDao.insertCountries(allEntities.map { it.country })
            countryDao.insertNativeNames(allEntities.flatMap { it.nativeNames })
            countryDao.insertCurrencies(allEntities.flatMap { it.currencies })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
