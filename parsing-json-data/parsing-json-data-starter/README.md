# Exercise 18: Task Manager with Complex JSON Parsing - Starter

## Overview

This starter code contains a basic Task Manager app with Retrofit integration. Your goal is to enhance it to handle complex JSON responses from an API with nested objects, nullable fields, and field name mismatches using Moshi annotations and custom adapters.

**Based on:** Exercise 17 (Setting up Retrofit)

## What You'll Learn

- Using Moshi annotations (`@JsonClass`, `@Json`) for field name mapping
- Handling nested JSON objects
- Creating custom Moshi adapters for special types (dates)
- Implementing null-safe parsing with default values
- Error handling for JSON parsing failures

## Getting Started

1. Open this project in Android Studio
2. Review the `sample_response.json` file to understand the complex JSON structure
3. Follow the TODO comments in the code files
4. Refer to the parent [README](../README.md) for detailed instructions

## Files to Complete

Complete the TODOs in the following order:

### Step 1: Add Moshi Annotations (TaskResponse.kt)
- Add `@JsonClass(generateAdapter = true)` to data classes
- Add `@Json(name = "field_name")` annotations to map JSON fields
- The annotations are already imported but not applied yet

### Step 2: Handle Nested Objects (TaskResponse.kt)
- Complete the `UserInfo` data class with Moshi annotations
- Ensure nested object parsing works correctly

### Step 3: Handle Nullable Fields (TaskResponse.kt)
- Use elvis operator (`?:`) in `toDomainModel()` for safe defaults
- Provide "Unknown" as default when user data is missing

### Step 4: Create Custom Date Adapter (DateAdapter.kt)
- Add `@FromJson` annotation to parse ISO 8601 date strings
- Add `@ToJson` annotation to serialize dates back to JSON
- Implement date parsing using SimpleDateFormat
- Handle errors gracefully by returning null for invalid dates

### Step 4 (continued): Register Custom Adapter (TasksService.kt)
- Register `DateAdapter` with Moshi using `.add()`
- Ensure `KotlinJsonAdapterFactory` is added last using `.addLast()`
- Order matters: custom adapters first, then KotlinJsonAdapterFactory

### Step 5: Handle Parsing Errors (TaskViewModel.kt)
- Catch `JsonDataException` for Moshi parsing errors
- Catch `IOException` for network errors
- Catch generic `Exception` for other errors
- Update sync state with appropriate error messages

## Testing Your Implementation

1. Run the app in Android Studio
2. Click the "Sync from API" button
3. The app should successfully fetch and display tasks from JSONPlaceholder API
4. Verify that:
   - Tasks are displayed correctly
   - The "created by" field shows user names (or "Unknown" if missing)
   - No crashes occur during JSON parsing
   - Error messages are shown if sync fails

## Expected Behavior

When complete, your app should:
- Parse complex JSON with nested objects seamlessly
- Map snake_case JSON fields (e.g., `created_at`) to camelCase Kotlin properties
- Handle nullable fields safely with defaults
- Parse ISO 8601 date strings using custom DateAdapter
- Display helpful error messages when parsing or network errors occur

## Reference

- See `sample_response.json` for the JSON structure
- Refer to the parent [Exercise README](../README.md) for detailed implementation guidance
- Check Moshi documentation for annotation reference

## Dependencies

All required dependencies are already added to `build.gradle.kts`:
- Retrofit with Moshi converter
- Moshi with Kotlin codegen (kapt)
- OkHttp logging interceptor

## Need Help?

If you get stuck:
1. Check the TODO comments for specific instructions
2. Review the parent [Exercise README](../README.md) for code examples
3. Examine `sample_response.json` to understand the JSON structure
4. Check Logcat for "OkHttp" to see API responses
5. Look for "TaskViewModel" logs to debug sync issues

Good luck! 🚀