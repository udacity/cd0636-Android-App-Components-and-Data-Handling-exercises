package com.udacity.project.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CountryViewModel : ViewModel() {

    private val _countries = MutableStateFlow(
        listOf(
            Country(
                name = "Turkmenistan",
                officialName = "Republic of Turkmenistan",
                capital = "Ashgabat",
                currencyCode = "TMT",
                currencyName = "Turkmenistan manat"
            ),
            Country(
                name = "Japan",
                officialName = "State of Japan",
                capital = "Tokyo",
                currencyCode = "JPY",
                currencyName = "Japanese yen"
            ),
            Country(
                name = "Brazil",
                officialName = "Federative Republic of Brazil",
                capital = "Brasilia",
                currencyCode = "BRL",
                currencyName = "Brazilian real"
            ),
            Country(
                name = "France",
                officialName = "French Republic",
                capital = "Paris",
                currencyCode = "EUR",
                currencyName = "Euro"
            ),
            Country(
                name = "Australia",
                officialName = "Commonwealth of Australia",
                capital = "Canberra",
                currencyCode = "AUD",
                currencyName = "Australian dollar"
            ),
            Country(
                name = "South Korea",
                officialName = "Republic of Korea",
                capital = "Seoul",
                currencyCode = "KRW",
                currencyName = "South Korean won"
            ),
            Country(
                name = "Kenya",
                officialName = "Republic of Kenya",
                capital = "Nairobi",
                currencyCode = "KES",
                currencyName = "Kenyan shilling"
            ),
            Country(
                name = "Mexico",
                officialName = "United Mexican States",
                capital = "Mexico City",
                currencyCode = "MXN",
                currencyName = "Mexican peso"
            ),
            Country(
                name = "India",
                officialName = "Republic of India",
                capital = "New Delhi",
                currencyCode = "INR",
                currencyName = "Indian rupee"
            ),
            Country(
                name = "Germany",
                officialName = "Federal Republic of Germany",
                capital = "Berlin",
                currencyCode = "EUR",
                currencyName = "Euro"
            )
        )
    )
    val countries: StateFlow<List<Country>> = _countries.asStateFlow()
}
