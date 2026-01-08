package com.fhzapps.bodytrack.networking

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseApi {
    @GET("api/v1/exercises/{exerciseId}")
    suspend fun getExerciseForId(
        @Path("exerciseId") exerciseId: String): Response<SingleExerciseResponse
            >

//    @GET("bodyparts/{bodyparts}/exercises")

    // 1. Fix the Return Type: It returns a List, not a wrapper object
    // 2. Fix the Path: Ensure it matches the standard /exercises/bodyPart/{id}
    @GET("api/v1/exercises/search")
    suspend fun getAllExercisesByBodyPart(
        @Query("search") searchQuery: String,
        @Query("limit") limit: Int = 10,  // Add default value
        @Query("offset") offset: Int = 0  // Add offset if needed
    ):  Response<ExercisesByBodyPartResponse >// CHANGE THIS to List<...>

//
//    @GET("exercises/bodyPart/{bodypart}") // Or whatever the RapidAPI endpoint is
//     suspend fun getAllExercisesByBodyPart(
//
//        @Path("bodypart") bodyPart: String,
//        @Query("limit") limit: Int): Response<ExercisesByBodyPartResponse
//            >
//    suspend fun getAllExercisesByBodyPart(@Path("bodyParts") bodyPart: String, @Query("limit") limit: Int, @Query("offset") offset: Int): Response<ExercisesByBodyPartResponse>

}