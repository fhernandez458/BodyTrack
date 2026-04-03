package com.fhzapps.bodytrack.data

import com.fhzapps.bodytrack.exercises.Movement
import kotlinx.coroutines.flow.Flow


interface ExerciseRepository {
    suspend fun getMovementById(exerciseId: String): Movement?
    fun getMovementsByBodyPart(bodyPart: String): Flow<List<MovementEntity>>
    suspend fun exerciseNameExists(name: String): Boolean
    suspend fun createExercise(
        name: String,
        bodyPart: String,
        instructions: String,
        equipment: List<String>,
        targetMuscles: List<String>,
    ): MovementEntity
}

/**
 * Repository module for handling data operations.
 * This class abstracts the data source (the Room DAO) from the rest of the app,
 * such as ViewModels.
 */
class ExerciseRepositoryImpl(
    private val exerciseDao: ExerciseDao,
    // private val exerciseApi: ExerciseApi, // uncomment when plugging in a new exercise API
) : ExerciseRepository {

    override suspend fun getMovementById(exerciseId: String): Movement? =
        exerciseDao.getMovementById(exerciseId)?.toMovement()

    override fun getMovementsByBodyPart(bodyPart: String): Flow<List<MovementEntity>> =
        exerciseDao.getMovementsByBodyPart(bodyPart)

    override suspend fun exerciseNameExists(name: String): Boolean =
        exerciseDao.exerciseNameExists(name)

    override suspend fun createExercise(
        name: String,
        bodyPart: String,
        instructions: String,
        equipment: List<String>,
        targetMuscles: List<String>,
    ): MovementEntity {
        val exerciseId = "custom_${name.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}"
        val movement = MovementEntity(
            exerciseId = exerciseId,
            name = name,
            bodyPart = bodyPart,
            instructions = instructions,
            targetSets = 3,
            targetReps = 12,
            lastMaxWeight = 0,
            lastMaxReps = 0,
            isUserCreated = true,
            targetMuscles = targetMuscles,
            secondaryMuscles = emptyList(),
            equipment = equipment,
            targetAreas = emptyList(),
            gifUrl = "",
        )
        exerciseDao.insertMovement(movement)
        return movement
    }

    // API-based implementations — uncomment when plugging in a new exercise API
    //
    // override suspend fun getExerciseByIdApi(exerciseId: String): Movement? {
    //     return try {
    //         val response = exerciseApi.getExerciseForId(exerciseId)
    //         response.body()?.data?.toExercise()
    //     } catch (e: Exception) {
    //         Log.e("ExerciseRepository", "Failed to fetch exercise with ID $exerciseId", e)
    //         null
    //     }
    // }
    //
    // override suspend fun getListOfExercisesForBodyPart(bodyPart: String, offset: Int): ExercisesByBodyPartResponse? {
    //     return try {
    //         val response = exerciseApi.getAllExercisesByBodyPart(searchQuery = bodyPart, limit = 25, offset = offset)
    //         response.body()
    //     } catch (e: Exception) {
    //         Log.e("ExerciseRepository", "Exception: ${e.message}")
    //         null
    //     }
    // }


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