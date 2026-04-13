package com.udacity.project.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project.app.data.db.CountryDao
import com.udacity.project.app.data.db.CountryDatabase
import com.udacity.project.app.data.db.CountryEntity
import com.udacity.project.app.data.db.CurrencyEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryDaoTest {

    private lateinit var database: CountryDatabase
    private lateinit var countryDao: CountryDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CountryDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        countryDao = database.countryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun databaseCreated() = runTest {
        val countries = countryDao.getAllCountriesWithDetails().first()
        assertThat(countries).isEmpty()
    }

    @Test
    fun deleteAllCountries_cascadesRelations() = runTest {
        val entity = CountryEntity("Japan", "Japan", "Tokyo")
        val currency =
            CurrencyEntity(countryName = "Japan", code = "JPY", name = "yen", symbol = "¥")
        countryDao.insertCountries(listOf(entity))
        countryDao.insertCurrencies(listOf(currency))

        countryDao.deleteAllCountries()

        val countries = countryDao.getAllCountriesWithDetails().first()
        assertThat(countries).isEmpty()
    }

    @Test
    fun insertDuplicate_replacesExisting() = runTest {
        countryDao.insertCountries(listOf(CountryEntity("Japan", "Japan", "Tokyo")))
        countryDao.insertCountries(listOf(CountryEntity("Japan", "State of Japan", "Tokyo")))

        val countries = countryDao.getAllCountriesWithDetails().first()
        assertThat(countries).hasSize(1)
        assertThat(countries[0].country.officialName).isEqualTo("State of Japan")
    }

    @Test
    fun insertAndRetrieveCountry() = runTest {
        val entity = CountryEntity(
            commonName = "Japan", officialName = "Japan", capitals = "Tokyo"
        )

        countryDao.insertCountries(listOf(entity))

        val countries = countryDao.getAllCountriesWithDetails().first()
        assertThat(countries).hasSize(1)
        assertThat(countries[0].country.commonName).isEqualTo("Japan")
        assertThat(countries[0].country.officialName).isEqualTo("Japan")
        assertThat(countries[0].country.capitals).isEqualTo("Tokyo")
    }
}
