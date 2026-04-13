# Retrofit Setup Solution

## Overview

This solution demonstrates configuring Retrofit for network requests in an Android app using Kotlin. It includes Retrofit setup with Moshi converter, logging interceptor for debugging, API service interface definition, and integration with ViewModel using coroutines.

## Implementation Details

### Part 1: Configure Retrofit

#### TODO 1.1: Create Retrofit Instance

**TasksService.kt**

```kotlin
object TasksService {
    /**
     * Base URL for JSONPlaceholder API.
     * IMPORTANT: Must end with a forward slash (/).
     */
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    /**
     * Moshi instance with Kotlin support.
     * KotlinJsonAdapterFactory enables proper serialization of Kotlin data classes.
     */
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * Retrofit instance configured with:
     * - Base URL: JSONPlaceholder API
     * - Moshi Converter: For JSON serialization/deserialization with Kotlin support
     *
     * Lazy initialization - created only when first accessed.
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
```

**Key Points:**
- Base URL must end with `/` - Retrofit appends endpoint paths to it
- Moshi with `KotlinJsonAdapterFactory` handles Kotlin data classes properly
- Lazy initialization defers creation until first use

#### TODO 1.2: Add Logging Interceptor

**TasksService.kt - Add logging configuration**

```kotlin
object TasksService {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    /**
     * Logging interceptor for debugging network requests.
     * Logs full request and response bodies to Logcat.
     * Filter Logcat by "OkHttp" to see network logs.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * OkHttpClient with logging interceptor and timeouts.
     * - Logging interceptor: Logs all HTTP traffic for debugging
     * - Connect timeout: 30 seconds to establish connection
     * - Read timeout: 30 seconds to read response data
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)  // Add OkHttpClient with logging
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
```

**Key Points:**
- `HttpLoggingInterceptor.Level.BODY` logs full request/response including headers and body
- Timeouts prevent indefinite hangs on slow/failed connections
- Filter Logcat by "OkHttp" to see interceptor logs

#### TODO 1.3: Create API Service

**TaskApiService.kt**

```kotlin
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API service interface for JSONPlaceholder /todos endpoints.
 * Retrofit generates implementation at runtime.
 */
interface TaskApiService {
    /**
     * Get all tasks from /todos endpoint.
     * Returns list of TaskDto objects.
     */
    @GET("todos")
    suspend fun getTasks(): List<TaskDto>

    /**
     * Get a single task by ID from /todos/{id} endpoint.
     * @param taskId The ID of the task to fetch
     */
    @GET("todos/{id}")
    suspend fun getTask(@Path("id") taskId: Int): TaskDto
}
```

**TaskDto.kt**

```kotlin
/**
 * Data Transfer Object for tasks from JSONPlaceholder API.
 * Matches the JSON structure returned by /todos endpoint.
 */
data class TaskDto(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val userId: Int
)
```

**TasksService.kt - Add API instance**

```kotlin
object TasksService {
    // ... existing retrofit setup ...

    /**
     * TaskApiService instance for making API calls.
     * Created from Retrofit using the interface definition.
     *
     * Usage:
     * val tasks = TasksService.api.getTasks()
     */
    val api: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }
}
```

**Key Points:**
- `@GET("todos")` creates GET request to `{BASE_URL}todos`
- `suspend` functions work with coroutines for async operations
- `@Path` annotation injects parameter into URL path
- Retrofit generates implementation automatically from interface

### Part 2: Integrate with ViewModel

#### TODO 2.1: Use Retrofit in syncTasks()

**TaskViewModel.kt**

```kotlin
fun syncTasks() {
    viewModelScope.launch {
        _syncState.value = SyncState.Loading
        try {
            // Use IO dispatcher for network operations
            val taskDtos = withContext(Dispatchers.IO) {
                TasksService.api.getTasks()
            }

            // Convert DTOs to domain Task objects
            val tasks = taskDtos.map { dto ->
                Task(
                    id = dto.id,
                    title = dto.title,
                    completed = dto.completed,
                    isFavorite = false  // API doesn't provide this field
                )
            }

            // Update ViewModel state
            savedStateHandle["tasks"] = tasks
            _syncState.value = SyncState.Success

        } catch (e: Exception) {
            // Handle network errors
            _syncState.value = SyncState.Error(e.message ?: "Sync failed")
        }
    }
}
```

**Key Points:**
- `withContext(Dispatchers.IO)` moves network call off main thread
- Map DTO (data layer) to domain model (business layer)
- Handle network errors with try-catch
- Update state to trigger UI updates

#### TODO 2.2: Test and Verify

**Testing Steps:**

1. **Run the app** - Launch on device/emulator

2. **Open Logcat** - View > Tool Windows > Logcat

3. **Filter logs** - Enter "OkHttp" in filter box

4. **Tap sync button** - Triggers network request

5. **Observe logs:**
   ```
   --> GET https://jsonplaceholder.typicode.com/todos
   --> END GET

   <-- 200 OK https://jsonplaceholder.typicode.com/todos (500ms)
   Content-Type: application/json; charset=utf-8
   [
     {"userId":1,"id":1,"title":"delectus aut autem","completed":false},
     {"userId":1,"id":2,"title":"quis ut nam facilis...","completed":false},
     ...
   ]
   <-- END HTTP (5645-byte body)
   ```

6. **Verify UI** - Tasks from API appear in RecyclerView

7. **Test error handling:**
   - Enable airplane mode
   - Tap sync button
   - Verify error message appears
   - Check Logcat for exception logs

## Architecture Patterns

### Separation of Concerns
- **TaskDto**: Data layer - matches API structure
- **Task**: Domain layer - app's business model
- **Mapping**: Converts between layers

### Network Layer
- **TasksService**: Singleton managing Retrofit instance
- **TaskApiService**: Interface defining endpoints
- **Interceptor**: Cross-cutting concern for logging

### ViewModel Integration
- Uses coroutines for async operations
- Dispatchers.IO for network calls
- State management with StateFlow
- Error handling with try-catch

## Common Issues and Solutions

### Issue: "Failed to resolve: retrofit"
**Solution**: Ensure dependencies are in build.gradle.kts:
```kotlin
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
```

### Issue: "Cannot serialize Kotlin type"
**Solution**: Add KotlinJsonAdapterFactory to Moshi:
```kotlin
val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
```

### Issue: "Base URL must end with /"
**Solution**: Ensure BASE_URL ends with forward slash:
```kotlin
private const val BASE_URL = "https://jsonplaceholder.typicode.com/"  // ✓
private const val BASE_URL = "https://jsonplaceholder.typicode.com"   // ✗
```

### Issue: "No logs in Logcat"
**Solution**:
- Filter Logcat by "OkHttp"
- Ensure logging interceptor level is set to BODY
- Check if interceptor is added to OkHttpClient

### Issue: "Network on main thread exception"
**Solution**: Use Dispatchers.IO:
```kotlin
withContext(Dispatchers.IO) {
    TasksService.api.getTasks()
}
```

## Key Takeaways

1. **Retrofit** simplifies HTTP API calls in Android
2. **Moshi** handles JSON serialization with Kotlin support
3. **Logging interceptor** is essential for debugging network issues
4. **Suspend functions** integrate seamlessly with coroutines
5. **DTO pattern** separates API structure from domain model
6. **Error handling** provides graceful failure recovery
7. **Dispatchers.IO** ensures network calls don't block UI