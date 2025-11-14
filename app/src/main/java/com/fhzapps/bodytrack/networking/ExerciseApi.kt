package com.fhzapps.bodytrack.networking

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseApi {
    @GET("exercises/{exerciseId}")
    suspend fun getExerciseForId(@Path("exerciseId") exerciseId: String): Response<SingleExerciseResponse>

    @GET("exercises/bodyparts") // limit of 25 max
    suspend fun getAllExercisesByBodyPart(@Query("bodyParts") bodyPart: String, @Query("limit") limit: Int): Response<ExercisesByBodyPartResponse>
//    suspend fun getAllExercisesByBodyPart(@Path("bodyParts") bodyPart: String, @Query("limit") limit: Int, @Query("offset") offset: Int): Response<ExercisesByBodyPartResponse>

}