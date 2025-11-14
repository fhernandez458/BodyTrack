package com.fhzapps.bodytrack.networking

import com.fhzapps.bodytrack.data.ExerciseResponse

data class ExercisesByBodyPartResponse(
    val exerciseList: MutableList<ExerciseResponse>,
    val metadata: Metadata,
    val success: Boolean,
)