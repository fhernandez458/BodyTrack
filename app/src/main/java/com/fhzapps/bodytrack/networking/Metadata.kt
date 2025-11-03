package com.fhzapps.bodytrack.networking

data class Metadata(
    val currentPage: Int,
    val nextPage: String,
    val previousPage: String,
    val totalExercises: Int,
    val totalPages: Int
)