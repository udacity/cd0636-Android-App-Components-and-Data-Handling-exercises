# Exercise 31: Testing the Data Layer - Solution

## Overview

This solution demonstrates comprehensive testing of the data layer in an
Android application using Room database. It includes unit tests with fake implementations,
instrumented tests with in-memory databases, and follows Android testing best practices.

## Test Structure

### Unit Tests (`src/test/`)
Unit tests run on the JVM and are fast. They use fake implementations to isolate the code under test.

#### 1. FakeTaskDao.kt
A fake implementation of `TaskDao` that stores data in memory using a `MutableList`.

**Key Features:**
- Implements all `TaskDao` interface methods
- Uses `MutableStateFlow` to emit updates through Flow
- Provides test helper methods (`clearAll()`, `getTasks()`)
- No actual database needed

**Benefits:**
- Fast execution (no database overhead)
- Full control over data state
- Easy to set up and tear down

#### 2. TaskRepositoryTest.kt
Tests the `TaskRepository` class using `FakeTaskDao`.

**Test Cases:**
- `addTask inserts task into database` - Verifies tasks are added correctly
- `getAllTasks returns all tasks from database` - Tests data retrieval
- `updateTask updates existing task in database` - Verifies updates work
- `deleteTask removes task from database` - Tests deletion
- `getAllTasks returns empty list when database is empty` - Edge case testing
- `addTask with favorite flag stores favorite correctly` - Tests all fields
- `multiple addTask operations store all tasks` - Tests multiple operations
- `deleteTask on non-existent task does not throw error` - Error handling

**Key Patterns:**
```kotlin
@Before
fun setup() {
    fakeDao = FakeTaskDao()
    repository = TaskRepository(fakeDao)
}

@After
fun tearDown() {
    fakeDao.clearAll()
}

@Test
fun `test name with spaces`() = runTest {
    // Given - setup test data
    // When - perform action
    // Then - verify result
}
```

#### 3. RepositoryLogicTest.kt
Additional tests focusing on repository business logic patterns.

**Test Cases:**
- Task conversion between domain and entity models
- Toggle pattern (like ViewModel uses)
- Batch operations
- Concurrent update scenarios
- Empty state handling

**Purpose:**
Tests realistic usage patterns that match how the ViewModel uses the repository.

### Instrumented Tests (`src/androidTest/`)
Instrumented tests run on an Android device/emulator and test actual Android framework components.

#### TaskDaoTest.kt
Tests the real `TaskDao` implementation using Room's in-memory database.

**Setup:**
```kotlin
database = Room.inMemoryDatabaseBuilder(
    context,
    TaskDatabase::class.java
)
    .allowMainThreadQueries() // Only for testing
    .build()

taskDao = database.taskDao()
```

**Test Cases:**
- `insertTask andGetAllTasks returnsTask` - Basic CRUD
- `getAllTasks returnsTasksOrderedByCreatedAtDescending` - Tests SQL ordering
- `updateTask updatesTaskInDatabase` - Tests @Update annotation
- `deleteTaskById removesTaskFromDatabase` - Tests @Query DELETE
- `insertTask withReplaceStrategy replacesExistingTask` - Tests OnConflictStrategy
- `insertTask withFavoriteFlag storesFavoriteCorrectly` - Tests all columns
- `deleteTaskById withNonExistentId doesNothing` - Edge cases
- `getAllTasks withEmptyDatabase returnsEmptyList` - Empty state
- `insertMultipleTasks andGetAllTasks returnsAllTasks` - Batch operations

**Key Differences from Unit Tests:**
- Uses real Room database
- Tests actual SQL queries
- Verifies Room annotations work correctly
- Tests database constraints and behavior

## Test Dependencies

### Added to build.gradle.kts:
```kotlin
// Unit testing
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("androidx.arch.core:core-testing:2.2.0")
testImplementation("com.google.truth:truth:1.1.5")

// Instrumented testing
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso.core:espresso-core:3.5.1")
androidTestImplementation("androidx.room:room-testing:2.6.1")
androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
androidTestImplementation("com.google.truth:truth:1.1.5")
```

## Key Testing Concepts

### 1. Test Doubles (Fakes)
**Fake**: A working implementation with shortcuts (e.g., in-memory storage instead of database)

```kotlin
class FakeTaskDao : TaskDao {
    private val tasks = mutableListOf<TaskEntity>()
    // ... implements all methods with in-memory storage
}
```

**Why Fakes over Mocks:**
- More realistic behavior
- Can be reused across multiple tests
- Easier to maintain
- Better represent actual usage

### 2. Test Coroutines
```kotlin
@Test
fun `test name`() = runTest {
    // runTest provides a test coroutine scope
    // Automatically waits for all coroutines to complete
    repository.addTask(task)
    val result = repository.getAllTasks().first()
}
```

**Key Tools:**
- `runTest` - Creates test scope for suspend functions
- `StandardTestDispatcher` - Provides controlled coroutine execution
- `.first()` - Collects first emission from Flow

### 3. In-Memory Database
```kotlin
Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
    .allowMainThreadQueries() // Only for testing!
    .build()
```

**Benefits:**
- Fast (no disk I/O)
- Isolated (each test gets fresh database)
- Real Room behavior
- Automatically cleaned up

### 4. Truth Assertions
```kotlin
import com.google.common.truth.Truth.assertThat

assertThat(tasks).hasSize(2)
assertThat(task.title).isEqualTo("Test")
assertThat(task.completed).isTrue()
assertThat(tasks).isEmpty()
```

**Why Truth:**
- More readable error messages
- Fluent API
- Better failure messages than JUnit assertions

### 5. Test Structure (Given-When-Then)
```kotlin
@Test
fun `descriptive test name`() = runTest {
    // Given - Set up test data and conditions
    val task = Task(1, "Test", false, false)

    // When - Perform the action being tested
    repository.addTask(task)

    // Then - Verify the result
    val tasks = repository.getAllTasks().first()
    assertThat(tasks).hasSize(1)
}
```

## Running the Tests

### Unit Tests (Fast)
```bash
./gradlew test
```
or
```bash
./gradlew :lesson-31-testing-the-data-layer:exercises:solution:test
```

### Instrumented Tests (Slower, needs device/emulator)
```bash
./gradlew connectedAndroidTest
```
or
```bash
./gradlew :lesson-31-testing-the-data-layer:exercises:solution:connectedAndroidTest
```

### Run All Tests
```bash
./gradlew test connectedAndroidTest
```

### In Android Studio
- Right-click on test class → "Run"
- Or click the green arrow next to test method
- View results in "Run" tool window

## Test Coverage

### What's Tested:
✅ Repository CRUD operations
✅ DAO database operations
✅ Entity-to-Domain model conversion
✅ Flow emissions and reactive updates
✅ Edge cases (empty database, non-existent IDs)
✅ SQL query ordering
✅ Room annotations (@Insert, @Update, @Query, @Delete)
✅ OnConflictStrategy behavior

### What's Not Tested:
❌ UI layer (MainActivity, Adapter)
❌ ViewModel (requires dependency injection for proper testing)
❌ Database migrations
❌ Complex SQL joins (not present in simple app)

## Best Practices Demonstrated

1. **Isolation**: Each test is independent and doesn't affect others
2. **Fast Feedback**: Unit tests run quickly on JVM
3. **Readable Names**: Test names describe what they test
4. **Setup/Teardown**: Proper cleanup in `@After` methods
5. **Test Doubles**: Using fakes instead of complex mocking frameworks
6. **Realistic Tests**: Testing actual usage patterns
7. **Edge Cases**: Testing empty states and error conditions
8. **Documentation**: Comments explain test purpose

## Common Pitfalls Avoided

1. ❌ **Not cleaning up after tests**
   ✅ Always call `fakeDao.clearAll()` and `database.close()`

2. ❌ **Testing implementation details**
   ✅ Test behavior, not internal structure

3. ❌ **Overly complex test setup**
   ✅ Keep tests simple and focused

4. ❌ **Not testing edge cases**
   ✅ Test empty states, null cases, and errors

5. ❌ **Forgetting to use runTest for coroutines**
   ✅ Always wrap suspend function tests in `runTest`

6. ❌ **Using real database in unit tests**
   ✅ Use fakes for unit tests, real database for instrumented tests

## Architecture for Testability

### Dependency Injection (Manual)
```kotlin
class TaskRepository(private val taskDao: TaskDao) {
    // TaskDao is injected, not created internally
}
```

This allows us to pass `FakeTaskDao` in tests.

### Separation of Concerns
- **Entity**: Database model (`TaskEntity`)
- **Domain**: Business model (`Task`)
- **Conversion**: Extension functions (`toEntity()`, `toDomainModel()`)

This separation makes testing easier and keeps concerns separated.

### Interface-Based Design
```kotlin
interface TaskDao {
    fun getAllTasks(): Flow<List<TaskEntity>>
    // ...
}
```

Interfaces allow for multiple implementations (real and fake).

## Next Steps

To make the app even more testable:

1. **Add Dependency Injection** (Hilt/Dagger)
   - Makes ViewModel testing easier
   - Centralized dependency management

2. **Add Network Testing**
   - Mock API responses
   - Test error handling

3. **Add UI Tests** (Espresso)
   - Test user interactions
   - End-to-end scenarios

4. **Measure Code Coverage**
   - Use JaCoCo or similar tools
   - Aim for 70-80% coverage

5. **Continuous Integration**
   - Run tests automatically on commits
   - Catch issues early

## Summary

This solution demonstrates production-ready testing patterns for Android's data layer:
- **Fast unit tests** with fake implementations
- **Reliable instrumented tests** with real Room database
- **Clear test structure** with Given-When-Then
- **Comprehensive coverage** of CRUD operations and edge cases
- **Best practices** for maintainable test code

The tests provide confidence that the data layer works correctly and serve as living documentation of expected behavior.
