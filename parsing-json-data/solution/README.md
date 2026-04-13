# Task Manager with Complex JSON Parsing - Solution

## Overview

This solution demonstrates production-ready JSON parsing using Moshi with support for complex API responses including nested objects, nullable fields, field name mapping, and custom type adapters. The implementation follows best practices for handling real-world API responses that may have incomplete or differently formatted data.

## Implementation Guide

This guide provides detailed step-by-step instructions for completing each TODO in the exercise.

### Part 1: Handle Complex JSON with Moshi Annotations

#### TODO 1.1: Add Moshi Annotations for Field Name Mapping

**File:** `TaskResponse.kt`

Create a data class with Moshi annotations to handle snake_case JSON fields:

```kotlin
package com.udacity.project.app

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response object for tasks with Moshi annotations.
 * Handles field name mapping from snake_case JSON to camelCase Kotlin.
 */
@JsonClass(generateAdapter = true)
data class TaskResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "completed") val completed: Boolean,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "due_date") val dueDate: String?
)
```

**Key Points:**
- `@JsonClass(generateAdapter = true)` enables Moshi code generation for better performance
- `@Json(name = "...")` maps JSON field names to Kotlin properties
- Nullable fields use `?` type to handle optional data from API
- Handles snake_case to camelCase conversion automatically

**Build Configuration:**

Ensure Moshi codegen is configured in `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.kotlin.ksp)
}

dependencies {
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
}
```

---

#### TODO 1.2: Handle Nested JSON Objects

**File:** `TaskDto.kt`

Update data classes to handle nested JSON structures:

```kotlin
package com.udacity.project.app

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object for tasks with nested user information.
 */
@JsonClass(generateAdapter = true)
data class TaskDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "completed") val completed: Boolean,
    @Json(name = "user") val user: UserInfo?,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "due_date") val dueDate: String?
)

/**
 * Nested user information from the API.
 */
@JsonClass(generateAdapter = true)
data class UserInfo(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String?
)
```

**Key Points:**
- Moshi automatically parses nested JSON objects
- `UserInfo?` is nullable in case the API doesn't return user data
- Nested objects also need `@JsonClass` and `@Json` annotations
- Email is nullable as it may not be provided for all users

**Example JSON:**
```json
{
  "id": 1,
  "title": "Complete project",
  "completed": false,
  "user": {
    "id": 10,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "created_at": "2024-01-15T10:30:00Z",
  "due_date": null
}
```

---

#### TODO 1.3: Handle Nullable Fields and Provide Defaults

**File:** `TaskViewModel.kt` or mapping file

Create extension function to convert DTOs to domain models with null safety:

```kotlin
/**
 * Converts TaskResponse DTO to domain Task model.
 * Provides sensible defaults for nullable fields.
 */
fun TaskResponse.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        completed = completed,
        createdBy = user?.name ?: "Unknown",
        dueDate = dueDate  // Will be parsed by DateAdapter
    )
}
```

Or if using TaskDto:

```kotlin
/**
 * Converts TaskDto to domain Task model.
 * Handles nullable nested objects and fields.
 */
fun TaskDto.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        completed = completed,
        createdBy = user?.name ?: "Unknown",
        createdAt = createdAt,
        dueDate = dueDate
    )
}
```

**Key Points:**
- Use elvis operator (`?:`) to provide defaults for nullable fields
- Safe navigation (`?.`) prevents crashes when nested objects are null
- Domain models may have different requirements than DTOs
- This pattern separates network layer from domain layer

---

### Part 2: Create Custom Adapters for Special Types

#### TODO 2.1: Create Custom Moshi Adapter for Date Parsing

**File:** `DateAdapter.kt`

Create a custom adapter to parse ISO 8601 date strings:

```kotlin
package com.udacity.project.app

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Custom Moshi adapter for parsing ISO 8601 date strings.
 * Handles conversion between String and Date objects.
 */
class DateAdapter {

    private val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        Locale.US
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * Parses ISO date string from JSON to Date object.
     * Returns null if parsing fails or input is null.
     */
    @FromJson
    fun fromJson(dateString: String?): Date? {
        if (dateString == null) return null
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Converts Date object to ISO date string for JSON.
     * Returns null if input is null.
     */
    @ToJson
    fun toJson(date: Date?): String? {
        if (date == null) return null
        return try {
            dateFormat.format(date)
        } catch (e: Exception) {
            null
        }
    }
}
```

**Register the adapter in `TasksService.kt`:**

```kotlin
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object TasksService {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // Configure Moshi with custom adapter
    private val moshi = Moshi.Builder()
        .add(DateAdapter())  // Add custom adapter first
        .addLast(KotlinJsonAdapterFactory())  // Add Kotlin support last
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // ... rest of the code
}
```

**Key Points:**
- `@FromJson` handles JSON → Kotlin conversion
- `@ToJson` handles Kotlin → JSON conversion
- Add custom adapter before `KotlinJsonAdapterFactory`
- SimpleDateFormat must be configured with timezone for consistency
- Graceful null handling prevents crashes

**Supported Date Format:**
- ISO 8601: `2024-01-15T10:30:00Z`
- UTC timezone
- Full date and time

---

#### TODO 2.2: Handle Parsing Errors Gracefully

**File:** `TaskViewModel.kt`

Update the sync function to handle different error types:

```kotlin
import com.squareup.moshi.JsonDataException
import java.io.IOException

fun syncTasks() {
    viewModelScope.launch {
        _syncState.value = SyncState.Loading
        try {
            // Network call on IO dispatcher
            val taskDtos = withContext(Dispatchers.IO) {
                TasksService.api.getTasks()
            }

            // Convert DTOs to domain models
            val tasks = taskDtos.take(20).map { dto ->
                dto.toDomainModel()
            }

            // Update task list
            _tasks.clear()
            _tasks.addAll(tasks)

            // Update next task ID
            nextTaskId = tasks.maxOfOrNull { it.id }?.plus(1) ?: 1

            _syncState.value = SyncState.Success
            Log.d("TaskViewModel", "Synced ${tasks.size} tasks from API")

        } catch (e: JsonDataException) {
            // Moshi parsing error - invalid JSON structure
            _syncState.value = SyncState.Error("Invalid data format")
            Log.e("TaskViewModel", "JSON parsing error", e)

        } catch (e: IOException) {
            // Network error - connection issues
            _syncState.value = SyncState.Error("Network error")
            Log.e("TaskViewModel", "Network error", e)

        } catch (e: Exception) {
            // Generic fallback for unexpected errors
            _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            Log.e("TaskViewModel", "Sync failed", e)
        }
    }
}
```

**Key Points:**
- Specific error handling provides better user feedback
- `JsonDataException` catches Moshi parsing failures
- `IOException` catches network problems
- Generic `Exception` catches unexpected issues
- Log errors for debugging
- Update state appropriately for each error type

**Error Types:**
1. **JsonDataException**: API returned invalid/unexpected JSON structure
2. **IOException**: Network connectivity issues, timeouts
3. **Exception**: Other unexpected errors

---

## Testing the Implementation

### Test Case 1: Valid JSON with All Fields

**Sample JSON:**
```json
{
  "id": 1,
  "title": "Complete Android project",
  "completed": false,
  "user": {
    "id": 10,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "created_at": "2024-01-15T10:30:00Z",
  "due_date": "2024-01-20T17:00:00Z"
}
```

**Expected Result:**
- All fields parsed correctly
- User name displayed as "John Doe"
- Dates converted to Date objects

---

### Test Case 2: JSON with Missing Nullable Fields

**Sample JSON:**
```json
{
  "id": 2,
  "title": "Review code",
  "completed": true,
  "user": null,
  "created_at": null,
  "due_date": null
}
```

**Expected Result:**
- Task created successfully
- User displayed as "Unknown" (default)
- No crashes from null values

---

### Test Case 3: Nested User with Missing Email

**Sample JSON:**
```json
{
  "id": 3,
  "title": "Write documentation",
  "completed": false,
  "user": {
    "id": 20,
    "name": "Jane Smith"
  },
  "created_at": "2024-01-16T09:00:00Z",
  "due_date": null
}
```

**Expected Result:**
- User name parsed correctly
- Missing email handled gracefully

---

### Test Case 4: Invalid JSON Structure

**Sample JSON:**
```json
{
  "id": "not-a-number",
  "title": 123
}
```

**Expected Result:**
- `JsonDataException` caught
- Error message: "Invalid data format"
- App doesn't crash

---

## Architecture Benefits

### 1. Separation of Concerns
- **DTO Layer**: Network response objects with Moshi annotations
- **Domain Layer**: Business models without JSON dependencies
- **Mapping Layer**: Conversion functions between layers

### 2. Null Safety
- All nullable fields explicitly marked with `?`
- Sensible defaults prevent crashes
- Safe navigation operators used throughout

### 3. Error Handling
- Specific error types caught separately
- User-friendly error messages
- Detailed logging for debugging

### 4. Performance
- Code generation via `@JsonClass` is faster than reflection
- Custom adapters cached and reused
- Efficient date parsing

### 5. Maintainability
- Clear field name mappings
- Type-safe parsing
- Easy to extend with new fields

---

## Common Issues and Solutions

### Issue 1: Moshi Can't Find Adapter

**Error:** `Platform class java.util.Date requires explicit JsonAdapter`

**Solution:** Register `DateAdapter` with Moshi:
```kotlin
.add(DateAdapter())
```

---

### Issue 2: KSP Not Generating Adapters

**Error:** `Cannot find implementation for @JsonClass`

**Solution:**
1. Add KSP plugin to build.gradle.kts
2. Use `ksp()` instead of `kapt()`
3. Sync project

---

### Issue 3: Field Names Don't Match

**Error:** Values are null despite being in JSON

**Solution:** Use `@Json(name = "...")` to map field names:
```kotlin
@Json(name = "user_id") val userId: Int
```

---

### Issue 4: Date Parsing Fails

**Error:** Dates are null or parsing throws exception

**Solution:**
1. Verify date format matches SimpleDateFormat pattern
2. Check timezone is set correctly
3. Handle exceptions in adapter

---

## Summary

This implementation demonstrates:
- ✅ Field name mapping with `@Json` annotations
- ✅ Nested JSON object parsing
- ✅ Nullable field handling with defaults
- ✅ Custom type adapters for dates
- ✅ Comprehensive error handling
- ✅ Separation between DTO and domain models
- ✅ Production-ready JSON parsing patterns

The solution is robust, type-safe, and ready for real-world API integration.