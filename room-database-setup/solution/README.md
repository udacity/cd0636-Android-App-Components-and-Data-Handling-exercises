# Task Manager with Room Database Setup - Solution

## Overview

This solution demonstrates how to set up Room database infrastructure in an Android application. It covers creating database entities with proper annotations, defining a DAO interface, and implementing the database singleton pattern. Room provides compile-time SQL verification, reducing runtime errors and simplifying database access.

## Implementation Guide

This guide provides detailed step-by-step instructions with complete code for each TODO.

### Part 1: Configure Room Dependencies

#### TODO 1: Add Room Dependencies

**File:** `build.gradle.kts`

Add Room dependencies and enable the KSP plugin:

```kotlin
plugins {
    alias(libs.plugins.kotlin.ksp)
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
```

**Key Points:**
- Room runtime provides the core database functionality
- Room KTX adds Kotlin coroutine and Flow support
- Room compiler (via KSP) generates implementation code at compile time
- KSP is preferred over kapt for faster build times

---

### Part 2: Create Database Entity

#### TODO 2: Create TaskEntity

**File:** `TaskEntity.kt`

Define the database entity that maps to the `tasks` table:

```kotlin
package com.udacity.project.app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for the tasks table.
 * Separated from domain Task model for clean architecture.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "completed")
    val completed: Boolean,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
```

**Add conversion functions** between entity and domain models:

```kotlin
fun TaskEntity.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        completed = completed,
        isFavorite = isFavorite
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        completed = completed,
        isFavorite = isFavorite,
        createdAt = System.currentTimeMillis()
    )
}
```

**Key Points:**
- `@Entity(tableName = "tasks")` maps this class to a database table named "tasks"
- `@PrimaryKey` marks `id` as the unique identifier for each row
- `@ColumnInfo(name = "...")` maps Kotlin properties to column names (snake_case convention)
- Default values (`false`, `System.currentTimeMillis()`) provide sensible defaults for new records
- Entity is separate from the domain `Task` model for clean architecture
- Extension functions handle conversion between layers

**Why Separate Entity and Domain Models:**
- Entity has database-specific annotations (Room concerns)
- Domain model is a clean data class (business logic)
- Changes to database schema don't ripple through the entire app
- Each layer has exactly the fields it needs

---

### Part 3: Create DAO Interface

#### TODO 3: Create TaskDao

**File:** `TaskDao.kt`

Create the DAO interface as a placeholder for future query methods:

```kotlin
package com.udacity.project.app

import androidx.room.Dao

/**
 * Data Access Object for Task database operations.
 * Query methods will be added in the next lesson.
 */
@Dao
interface TaskDao
```

**Key Points:**
- `@Dao` annotation marks this as a Room Data Access Object
- Room validates all DAO methods at compile time
- Interface will be implemented by Room's code generator
- Query methods will be added in the next lesson

---

### Part 4: Configure Database

#### TODO 4: Create TaskDatabase

**File:** `TaskDatabase.kt`

Configure the Room database with singleton pattern:

```kotlin
package com.udacity.project.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for Task Manager.
 * Uses singleton pattern to ensure single instance.
 */
@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

**Key Points:**
- `@Database` annotation registers entities and sets the schema version
- `entities = [TaskEntity::class]` lists all tables in this database
- `exportSchema = false` disables schema export (set to `true` for migrations)
- `version = 1` tracks schema version for migrations
- Abstract class extends `RoomDatabase` — Room generates the implementation
- Abstract function `taskDao()` provides access to the DAO
- Singleton pattern ensures only one database instance exists

**Singleton Pattern Explained:**
- `@Volatile` ensures INSTANCE is immediately visible to all threads
- `synchronized(this)` prevents multiple threads from creating duplicate instances
- Double-checked locking: first check avoids synchronization overhead, second check prevents race conditions
- `context.applicationContext` prevents memory leaks from activity references

---

## Architecture Diagram

```
┌─────────────────────────┐
│       Domain Layer       │
│                         │
│   Task (data class)     │
│   - id, title, completed│
└────────────┬────────────┘
             │ toEntity() / toDomainModel()
             ▼
┌─────────────────────────┐
│      Database Layer      │
│                         │
│   TaskEntity (@Entity)  │
│   - id, title, completed│
│   - isFavorite, createdAt│
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│       TaskDao (@Dao)     │
│   (empty - queries next) │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│    TaskDatabase          │
│    (Singleton)           │
│                         │
│  Room.databaseBuilder() │
│  → task_database file   │
└─────────────────────────┘
```

## Testing the Implementation

### Test Case 1: Build Verification
1. Build the project
2. No compilation errors should occur
3. Room validates entity annotations at compile time

### Test Case 2: Entity Annotations
1. Verify `@Entity` creates a "tasks" table
2. Verify `@PrimaryKey` is set on `id`
3. Verify `@ColumnInfo` maps column names correctly

### Test Case 3: Database Singleton
1. Call `TaskDatabase.getDatabase(context)` multiple times
2. Verify the same instance is returned each time
3. Verify thread safety with concurrent access

### Test Case 4: Model Conversion
1. Create a `Task` domain model
2. Convert to `TaskEntity` using `toEntity()`
3. Convert back using `toDomainModel()`
4. Verify all shared fields match

## Common Issues and Solutions

### Issue 1: KSP Not Processing Annotations

**Error:** `Cannot find implementation for TaskDatabase`

**Solution:**
1. Ensure KSP plugin is applied in `build.gradle.kts`
2. Use `ksp(libs.room.compiler)` instead of `kapt`
3. Sync and rebuild project

---

### Issue 2: Entity Without Primary Key

**Error:** `An entity must have at least 1 field annotated with @PrimaryKey`

**Solution:** Add `@PrimaryKey` to the `id` field:
```kotlin
@PrimaryKey
val id: Int
```

---

### Issue 3: Database Leak Warning

**Error:** `database connection pool leak detected`

**Solution:** Use `context.applicationContext` in `Room.databaseBuilder()` to avoid holding references to destroyed activities.

---

## Summary

This implementation demonstrates:
- ✅ Room dependencies configured with KSP
- ✅ TaskEntity with @Entity, @PrimaryKey, @ColumnInfo annotations
- ✅ TaskDao interface with @Dao annotation
- ✅ TaskDatabase with singleton pattern and thread safety
- ✅ Clean separation between entity and domain models
- ✅ Extension functions for model conversion
- ✅ Compile-time SQL verification through Room

The database infrastructure is now ready for CRUD operations, which will be implemented in the next lesson.
