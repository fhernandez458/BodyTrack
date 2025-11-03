package com.fhzapps.bodytrack.networking

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("/exercises/{exerciseId}")
    fun getExerciseForId(@Path("exerciseId") exerciseId: String): Call<SingleExerciseResponse>

    @GET("/bodyparts/{bodyParts}/exercises") // limit of 25 max
    fun getAllExercisesByBodyPart(@Path("bodyParts") bodyPart: String, @Query("limit") limit: Int, @Query("offset") offset: Int): Call<ExercisesByBodyPartResponse>

}