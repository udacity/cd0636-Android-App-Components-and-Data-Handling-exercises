package com.udacity.project.app

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    val id: Int,
    val name: String,
    val officialName: String,
    val capital: String,
    val currencyName: String,
    val isFavorite: Boolean = false
) : Parcelable
