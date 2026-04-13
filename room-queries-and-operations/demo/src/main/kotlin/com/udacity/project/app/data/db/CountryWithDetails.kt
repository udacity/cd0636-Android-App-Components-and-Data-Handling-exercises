package com.udacity.project.app.data.db

import androidx.room.Embedded
import androidx.room.Relation
import com.udacity.project.app.data.repo.Country

data class CountryWithDetails(
    @Embedded val country: CountryEntity,
    @Relation(parentColumn = "common_name", entityColumn = "country_name")
    val nativeNames: List<NativeNameEntity>,
    @Relation(parentColumn = "common_name", entityColumn = "country_name")
    val currencies: List<CurrencyEntity>
)

fun CountryWithDetails.toDomainModel(): Country {
    return country.toDomainModel(currencies, nativeNames)
}