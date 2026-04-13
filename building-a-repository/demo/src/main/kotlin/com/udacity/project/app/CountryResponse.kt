package com.udacity.project.app

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryResponse(
    @Json(name = "name") val nameInfo: NameInfo,
    @Json(name = "capital") val capitalCities: List<String>,
    @Json(name = "currencies") val currencyList: List<CurrencyInfo>
)

@JsonClass(generateAdapter = true)
data class NameInfo(
    @Json(name = "common") val commonName: String,
    @Json(name = "official") val officialName: String
)

@JsonClass(generateAdapter = true)
data class CurrencyInfo(
    @Json(name = "code") val currencyCode: String,
    @Json(name = "name") val currencyName: String,
    @Json(name = "symbol") val currencySymbol: String?
)

fun CountryResponse.toDomainModel(): Country {
    return Country(
        name = nameInfo.commonName,
        officialName = nameInfo.officialName,
        capital = capitalCities.firstOrNull() ?: "N/A",
        currencyCode = currencyList.firstOrNull()?.currencyCode ?: "N/A",
        currencyName = currencyList.firstOrNull()?.let { currency ->
            if (currency.currencySymbol != null) "${currency.currencyName} (${currency.currencySymbol})"
            else currency.currencyName
        } ?: "Unknown"
    )
}
