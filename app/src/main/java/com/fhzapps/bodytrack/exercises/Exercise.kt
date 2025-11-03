package com.fhzapps.bodytrack.exercises

class Exercise(
    val name: String,
    val exerciseId: String,
    val instructions: String,
    val targetSets: Int,
    val targetReps: Int,
    val lastMaxWeight: Int,
    val lastMaxReps: Int,
    val isUserCreated: Boolean = false,
    val targetMuscles: List<String> = listOf(),
    val secondaryMuscles: List<String> = listOf(),
    val equipment: List<String> = listOf(),
    val targetAreas: List<String> = listOf(),
    val gifUrl : String = "",
) {
    companion object {
        val DEFAULT = Exercise(
            name = "DEFAULT",
            exerciseId = "",
            instructions = "",
            targetSets = 0,
            targetReps = 0,
            lastMaxWeight = 0,
            lastMaxReps = 0,
            isUserCreated = false,
            gifUrl = "https://static.exercisedb.dev/media/wnEscH8.gif"
        )
    }
}