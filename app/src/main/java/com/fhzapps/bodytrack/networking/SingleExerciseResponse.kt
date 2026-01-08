package com.fhzapps.bodytrack.networking

import com.fhzapps.bodytrack.data.ExerciseResponse

data class SingleExerciseResponse(
    val data: ExerciseResponse,
    val success: Boolean
)