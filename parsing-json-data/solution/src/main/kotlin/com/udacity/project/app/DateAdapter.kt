package com.udacity.project.app

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Custom Moshi adapter for parsing ISO 8601 date strings to Date objects.
 *
 * Demonstrates:
 * - @FromJson: Converts JSON string to Kotlin type
 * - @ToJson: Converts Kotlin type to JSON string
 * - Null safety: Returns null for invalid dates
 * - Error handling: Catches parsing exceptions gracefully
 *
 * Supported format: yyyy-MM-dd'T'HH:mm:ss'Z' (ISO 8601 UTC)
 * Example: "2024-01-15T10:30:00Z"
 */
class DateAdapter {

    /**
     * Parses ISO 8601 date string from JSON to Date object.
     * Returns null if the date string is null or parsing fails.
     */
    @FromJson
    fun fromJson(dateString: String?): Date? {
        if (dateString == null) return null

        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(dateString)
        } catch (e: Exception) {
            // Return null for invalid date formats instead of crashing
            null
        }
    }

    /**
     * Converts Date object to ISO 8601 string for JSON serialization.
     * Returns null if the date is null.
     */
    @ToJson
    fun toJson(date: Date?): String? {
        if (date == null) return null

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(date)
    }
}