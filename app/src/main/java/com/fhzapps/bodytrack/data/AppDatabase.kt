package com.fhzapps.bodytrack.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The main database class for the application.
 *
 * This class is annotated with @Database and lists all the entities (tables)
 * and the database version. It also contains abstract methods to get the DAOs.
 */
@Database(
    entities = [
        ExerciseData::class, // The table for workout sessions
        SetData::class       // The table for individual sets
    ],
    version = 1, // Start at version 1. Increment this number whenever you change the schema.
    exportSchema = false // Optional: Set to true if you want to export the schema to a folder.
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * This abstract function provides an instance of the ExerciseDao.
     * Room will generate the implementation for this method.
     */
    abstract fun exerciseDao(): ExerciseDao

    // If you had other DAOs, you would add them here as well.
    // abstract fun anotherDao(): AnotherDao
}