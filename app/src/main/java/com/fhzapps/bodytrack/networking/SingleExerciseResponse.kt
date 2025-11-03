package com.fhzapps.bodytrack.networking

import com.fhzapps.bodytrack.data.ExerciseResponse

data class SingleExerciseResponse(
    val exerciseData: ExerciseResponse,
    val success: Boolean
)