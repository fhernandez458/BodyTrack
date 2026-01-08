package com.fhzapps.bodytrack.networking

import com.fhzapps.bodytrack.data.ExerciseListResponse

data class ExercisesByBodyPartResponse(
    val data: MutableList<ExerciseListResponse>,
    val metadata: Metadata,
    val success: Boolean,
)