package com.fhzapps.bodytrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_data_table")
data class ExerciseData (
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Using Long is better practice for IDs
    val exerciseId: String, // This likely references the base Exercise details
    val timestamp: Long = System.currentTimeMillis() // Good to know when it was performed
)