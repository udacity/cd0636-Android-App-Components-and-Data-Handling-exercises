package com.udacity.project.app

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * TODO: Step 4 - Create custom Moshi adapter for date parsing
 *
 * Custom Moshi adapter for parsing ISO 8601 date strings to Date objects.
 *
 * Instructions:
 * 1. Add @FromJson annotation to the fromJson method
 * 2. Add @ToJson annotation to the toJson method
 * 3. Implement the date parsing logic using SimpleDateFormat
 * 4. Use format pattern: "yyyy-MM-dd'T'HH:mm:ss'Z'"
 * 5. Set timezone to UTC
 * 6. Handle null values and exceptions gracefully
 *
 * Supported format: yyyy-MM-dd'T'HH:mm:ss'Z' (ISO 8601 UTC)
 * Example: "2024-01-15T10:30:00Z"
 */
class DateAdapter {

    /**
     * TODO: Add @FromJson annotation
     *
     * Parses ISO 8601 date string from JSON to Date object.
     * Returns null if the date string is null or parsing fails.
     *
     * Steps:
     * 1. Check if dateString is null and return null if so
     * 2. Create SimpleDateFormat with pattern "yyyy-MM-dd'T'HH:mm:ss'Z'" and Locale.US
     * 3. Set the timezone to UTC using TimeZone.getTimeZone("UTC")
     * 4. Parse the dateString using format.parse()
     * 5. Wrap in try-catch to handle parsing exceptions and return null on error
     */
    fun fromJson(dateString: String?): Date? {
        // TODO: Implement date parsing logic
        if (dateString == null) return null

        return try {
            // TODO: Create SimpleDateFormat with correct pattern and locale
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            // TODO: Set timezone to UTC
            format.timeZone = TimeZone.getTimeZone("UTC")
            // TODO: Parse and return the date
            format.parse(dateString)
        } catch (e: Exception) {
            // Return null for invalid date formats instead of crashing
            null
        }
    }

    /**
     * TODO: Add @ToJson annotation
     *
     * Converts Date object to ISO 8601 string for JSON serialization.
     * Returns null if the date is null.
     *
     * Steps:
     * 1. Check if date is null and return null if so
     * 2. Create SimpleDateFormat with pattern "yyyy-MM-dd'T'HH:mm:ss'Z'" and Locale.US
     * 3. Set the timezone to UTC
     * 4. Format the date using format.format()
     */
    fun toJson(date: Date?): String? {
        // TODO: Implement date serialization logic
        if (date == null) return null

        // TODO: Create SimpleDateFormat with correct pattern and locale
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        // TODO: Set timezone to UTC
        format.timeZone = TimeZone.getTimeZone("UTC")
        // TODO: Format and return the date string
        return format.format(date)
    }
}