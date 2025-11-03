package com.fhzapps.bodytrack.data

import com.fhzapps.bodytrack.exercises.Exercise

data class ExerciseResponse(
    val bodyParts: List<String>,
    val equipments: List<String>,
    val exerciseId: String,
    val gifUrl: String,
    val instructions: List<String>,
    val name: String,
    val secondaryMuscles: List<String>,
    val targetMuscles: List<String>
)

fun ExerciseResponse.toExercise(): Exercise {
    return Exercise(
        name = this.name,
        gifUrl = this.gifUrl,
        equipment = this.equipments,
        instructions = this.instructions.joinToString(separator = "\n"),
        exerciseId = this.exerciseId,
        secondaryMuscles = this.secondaryMuscles,
        targetMuscles = this.targetMuscles,
        targetSets = 3,
        targetReps = 15,
        lastMaxWeight = 0,
        lastMaxReps = 0,
        isUserCreated = false,
    )
}