package com.fhzapps.bodytrack.data.workout

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.fhzapps.bodytrack.data.MovementEntity

@Entity(
    tableName = "workout_exercise_table",
    primaryKeys = ["workoutId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MovementEntity::class,
            parentColumns = ["exerciseId"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("exerciseId")],
)
data class WorkoutExerciseEntity(
    val workoutId: Long,
    val exerciseId: String,
    val orderIndex: Int,
    val targetSets: Int = 3,
    val targetReps: Int = 12,
)
