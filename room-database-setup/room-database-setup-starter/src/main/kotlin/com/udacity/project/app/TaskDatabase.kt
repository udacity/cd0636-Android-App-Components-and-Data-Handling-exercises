package com.udacity.project.app

import android.content.Context

/**
 * TODO 4: Create TaskDatabase with Room annotations
 *
 * 1. Add @Database annotation with:
 *    - entities = [TaskEntity::class]
 *    - version = 1
 *    - exportSchema = false
 *
 * 2. Make this class abstract and extend RoomDatabase
 *
 * 3. Add abstract function: abstract fun taskDao(): TaskDao
 *
 * 4. Create singleton pattern in companion object:
 *    - Use @Volatile for INSTANCE
 *    - Implement getDatabase(context: Context) function
 *    - Use Room.databaseBuilder() to create instance
 *    - Use synchronized block for thread safety
 */
class TaskDatabase {
    // TODO 4.1: Implement Room database class following structure above
}