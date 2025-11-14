package com.fhzapps.bodytrack.data

import android.util.Log
import com.fhzapps.bodytrack.exercises.Exercise
import com.fhzapps.bodytrack.networking.ExerciseApi
import kotlinx.coroutines.flow.Flow


interface ExerciseRepository {
    suspend fun getExerciseByIdApi(exerciseId: String) : Exercise?
    suspend fun getAllExercisesForBodyPartApi(bodyPart: String) : MutableList<Exercise>?
}

/**
 * Repository module for handling data operations.
 * This class abstracts the data source (the Room DAO) from the rest of the app,
 * such as ViewModels.
 * ExerciseDb API does not support local caching, so make those calls direct to API.
 * store exercise/set data in local db
 */
class ExerciseRepositoryImpl (
    private val exerciseDao: ExerciseDao,
    private val exerciseApi: ExerciseApi
) : ExerciseRepository {


    //REMOTE API CALLS, API does not support local storage

    // Updated the function to return Exercise? and use a try-catch block
    override suspend fun getExerciseByIdApi(exerciseId: String): Exercise? {
        return try {
            // 1. Make the API call
            val response = exerciseApi.getExerciseForId(exerciseId)

            // 2. Convert the response body to an Exercise.
            // If the body or nested data is null, the ?. operator will gracefully
            // result in null, which will be returned by the function.
            Log.d("ExerciseRepository", "Successfully fetched exercise with ID $exerciseId, response: $response")

            response.body()?.exerciseData?.toExercise()
        } catch (e: Exception) {
            // 3. If any exception occurs (e.g., network issue), log it and return null.
            Log.e("ExerciseRepository", "Failed to fetch exercise with ID $exerciseId", e)
            null
        }
    }

    override suspend fun getAllExercisesForBodyPartApi(bodyPart: String): MutableList<Exercise>? {
        return try {
            val response = exerciseApi.getAllExercisesByBodyPart(bodyPart = bodyPart, limit = 25) //HARDCODING FOR TESTING PURPOSES
            val responseList = response.body()?.exerciseList
            Log.d("ExerciseRepository", "Successfully fetched exercises for body part $bodyPart, response: $response and body: ${response.body()}")
            Log.d("ExerciseRepository", "metadata: ${response.body()?.metadata}responseList: $responseList")
            responseList?.map { it.toExercise() }
        } catch (e: Exception) {
            Log.e("Exercise Repository","Exception: ${e.message}")
            mutableListOf<Exercise>()
        } as MutableList<Exercise>?
    }


    //LOCAL DATABASE OPERATIONS FOR FETCHING HISTORIC SET DATA

    fun getExercisesByDate(exerciseId: Long, startTime: Long, endTime: Long): Flow<List<ExerciseDataWithSets>> {
        return exerciseDao.getExercisesByDate(exerciseId, startTime, endTime)
    }


    fun getExerciseDataWithSets(exerciseDataId: Long): Flow<ExerciseDataWithSets> {
        return exerciseDao.getExerciseDataWithSets(exerciseDataId)
    }


    suspend fun insertExerciseWithSets(exerciseData: ExerciseData, sets: List<SetData>) {
        exerciseDao.insertExerciseWithSets(exerciseData, sets)
    }
}