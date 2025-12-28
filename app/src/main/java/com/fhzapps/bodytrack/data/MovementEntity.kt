package com.fhzapps.bodytrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fhzapps.bodytrack.exercises.Movement

@Entity(tableName = "movement_table")
data class MovementEntity(
    @PrimaryKey val exerciseId: String,
    val name: String,
    val instructions: String,
    val targetSets: Int,
    val targetReps: Int,
    val lastMaxWeight: Int,
    val lastMaxReps: Int,
    val isUserCreated: Boolean,
    val targetMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val equipment: List<String>,
    val targetAreas: List<String>,
    val gifUrl: String
)

fun MovementEntity.toMovement(): Movement {
    return Movement(
        name = name,
        exerciseId = exerciseId,
        instructions = instructions,
        targetSets = targetSets,
        targetReps = targetReps,
        lastMaxWeight = lastMaxWeight,
        lastMaxReps = lastMaxReps,
        isUserCreated = isUserCreated,
        targetMuscles = targetMuscles,
        secondaryMuscles = secondaryMuscles,
        equipment = equipment,
        targetAreas = targetAreas,
        gifUrl = gifUrl
    )
}

fun Movement.toEntity(): MovementEntity {
    return MovementEntity(
        exerciseId = exerciseId,
        name = name,
        instructions = instructions,
        targetSets = targetSets,
        targetReps = targetReps,
        lastMaxWeight = lastMaxWeight,
        lastMaxReps = lastMaxReps,
        isUserCreated = isUserCreated,
        targetMuscles = targetMuscles,
        secondaryMuscles = secondaryMuscles,
        equipment = equipment,
        targetAreas = targetAreas,
        gifUrl = gifUrl
    )
}
