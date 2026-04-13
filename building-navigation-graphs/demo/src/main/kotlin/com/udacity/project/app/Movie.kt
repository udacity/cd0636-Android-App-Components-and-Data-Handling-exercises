package com.udacity.project.app

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val watched: Boolean = false
) : Parcelable
