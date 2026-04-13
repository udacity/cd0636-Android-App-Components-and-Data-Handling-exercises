package com.udacity.project.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Transaction
    @Query("SELECT * FROM countries ORDER BY common_name ASC")
    fun getAllCountriesWithDetails(): Flow<List<CountryWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<CountryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNativeNames(nativeNames: List<NativeNameEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<CurrencyEntity>)

    @Query("DELETE FROM countries")
    suspend fun deleteAllCountries()

    @Transaction
    suspend fun refreshAllCountries(
        countries: List<CountryEntity>,
        nativeNames: List<NativeNameEntity>,
        currencies: List<CurrencyEntity>
    ) {
        deleteAllCountries()
        insertCountries(countries)
        insertNativeNames(nativeNames)
        insertCurrencies(currencies)
    }
}