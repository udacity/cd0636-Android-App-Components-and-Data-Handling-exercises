package com.udacity.project.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.project.app.data.repo.Country

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey
    @ColumnInfo(name = "common_name")
    val commonName: String,
    @ColumnInfo(name = "official_name")
    val officialName: String,
    @ColumnInfo(name = "capitals")
    val capitals: String
)

fun CountryEntity.toDomainModel(
    currencies: List<CurrencyEntity> = emptyList()
): Country {
    return Country(
        name = commonName,
        officialName = officialName,
        capital = capitals.split("|").firstOrNull() ?: "N/A",
        currencyCode = currencies.firstOrNull()?.code ?: "N/A",
        currencyName = currencies.firstOrNull()?.let { currency ->
            if (currency.symbol != null) "${currency.name} (${currency.symbol})"
            else currency.name
        } ?: "Unknown"
    )
}
