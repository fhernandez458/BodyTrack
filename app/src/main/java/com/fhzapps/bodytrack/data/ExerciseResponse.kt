package com.fhzapps.bodytrack.data

import com.fhzapps.bodytrack.exercises.Movement

data class ExerciseListResponse(
    val exerciseId: String,
    val name: String,
    val imageUrl: String
)
{
    companion object {
        val DEFAULT = ExerciseListResponse(
            exerciseId = "DEFAULT",
            name = "Chest Press",
            imageUrl = "https://static.exercisedb.dev/media/wnEscH8.gif"
        )
    }

}

data class ExerciseResponse(
val bodyParts: List<String>,
val equipments: List<String>,
val exerciseId: String,
val exerciseTips: List<String>,
val exerciseType: String,
val imageUrl: String,
val instructions: List<String>,
val keywords: List<String>,
val name: String,
val overview: String,
val relatedExerciseIds: List<String>,
val secondaryMuscles: List<String>,
val targetMuscles: List<String>,
val variations: List<String>,
val videoUrl: String
)


fun ExerciseResponse.toExercise(): Movement {
    return Movement(
        name = this.name,
        gifUrl = this.imageUrl,
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