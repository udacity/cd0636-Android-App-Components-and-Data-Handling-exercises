package com.udacity.project.app

// TODO: Step 1 - Make Task Parcelable for SavedStateHandle serialization
// Add these imports:
// import android.os.Parcelable
// import kotlinx.parcelize.Parcelize
//
// Add @Parcelize annotation above the data class
// Add : Parcelable after the class definition
// Result should be:
// @Parcelize
// data class Task(...) : Parcelable
//
// Note: Ensure kotlin-parcelize plugin is enabled in build.gradle.kts:
// plugins {
//     id("kotlin-parcelize")
// }
data class Task(
    val id: Int,
    val title: String,
    val completed: Boolean = false
)