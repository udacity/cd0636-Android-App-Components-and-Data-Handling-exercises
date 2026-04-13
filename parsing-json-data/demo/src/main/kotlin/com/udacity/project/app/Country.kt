package com.udacity.project.app

/**
 * Domain model for a Country.
 * This is separate from the API response (CountryResponse) — the domain model
 * is a flat structure that mirrors the app's needs, not the nested API structure.
 */
data class Country(
    val name: String,
    val officialName: String,
    val capital: String,
    val currencyCode: String,
    val currencyName: String
)
