package com.udacity.project.app.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.project.app.data.db.CountryEntity
import com.udacity.project.app.data.db.CurrencyEntity
import com.udacity.project.app.data.db.NativeNameEntity
import com.udacity.project.app.data.repo.Country

@JsonClass(generateAdapter = true)
data class CountryResponse(
    @Json(name = "name") val nameInfo: NameInfo,
    @Json(name = "capital") val capitalCities: List<String>,
    @Json(name = "currencies") val currencyList: List<CurrencyInfo>
)

@JsonClass(generateAdapter = true)
data class NameInfo(
    @Json(name = "common") val commonName: String,
    @Json(name = "official") val officialName: String,
    @Json(name = "nativeName") val nativeNames: List<NativeNameInfo> = emptyList()
)

@JsonClass(generateAdapter = true)
data class NativeNameInfo(
    val lang: String,
    val official: String,
    val common: String
)

@JsonClass(generateAdapter = true)
data class CurrencyInfo(
    @Json(name = "code") val currencyCode: String,
    @Json(name = "name") val currencyName: String,
    @Json(name = "symbol") val currencySymbol: String?
)

fun CountryResponse.toDomainModel(): Country {
    return Country(
        countryName = nameInfo.commonName,
        commonName = nameInfo.nativeNames.firstOrNull()?.common ?: "N/A",
        officialName = nameInfo.officialName,
        capital = capitalCities.firstOrNull() ?: "N/A",
        currencyCode = currencyList.firstOrNull()?.currencyCode ?: "N/A",
        currencyName = currencyList.firstOrNull()?.let { currency ->
            if (currency.currencySymbol != null) "${currency.currencyName} (${currency.currencySymbol})"
            else currency.currencyName
        } ?: "Unknown"
    )
}

data class CountryEntities(
    val country: CountryEntity,
    val nativeNames: List<NativeNameEntity>,
    val currencies: List<CurrencyEntity>
)

fun CountryResponse.toEntities(): CountryEntities {
    val countryEntity = CountryEntity(
        commonName = nameInfo.commonName,
        officialName = nameInfo.officialName,
        capitals = capitalCities.joinToString("|")
    )

    val nativeNameEntities = nameInfo.nativeNames.map { nativeName ->
        NativeNameEntity(
            countryName = nameInfo.commonName,
            lang = nativeName.lang,
            official = nativeName.official,
            common = nativeName.common
        )
    }

    val currencyEntities = currencyList.map { currency ->
        CurrencyEntity(
            countryName = nameInfo.commonName,
            code = currency.currencyCode,
            name = currency.currencyName,
            symbol = currency.currencySymbol
        )
    }

    return CountryEntities(countryEntity, nativeNameEntities, currencyEntities)
}
