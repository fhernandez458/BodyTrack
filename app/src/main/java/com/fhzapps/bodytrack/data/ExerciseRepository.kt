package com.fhzapps.bodytrack.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository module for handling data operations.
 * This class abstracts the data source (the Room DAO) from the rest of the app,
 * such as ViewModels.
 */
class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    /**
     * Retrieves all workout sessions for a specific exercise performed on a specific day.
     * @param exerciseId The unique ID of the exercise to filter by.
     * @param startTime The beginning of the day.
     * @param endTime The end of the day.
     * @return A Flow emitting a list of workout sessions for that specific exercise and date.
     */
    fun getExercisesByDate(exerciseId: Long, startTime: Long, endTime: Long): Flow<List<ExerciseDataWithSets>> {
        return exerciseDao.getExercisesByDate(exerciseId, startTime, endTime)
    }

    /**
     * Retrieves a single workout session by its unique ID, including all its sets.
     * @param exerciseDataId The unique ID of the ExerciseData record.
     * @return A Flow emitting the specific ExerciseDataWithSets.
     */
    fun getExerciseDataWithSets(exerciseDataId: Long): Flow<ExerciseDataWithSets> {
        return exerciseDao.getExerciseDataWithSets(exerciseDataId)
    }

    /**
     * Inserts a new workout session along with all its associated sets into the database.
     * This operation is transactional, meaning it will either complete entirely or not at all.
     * @param exerciseData The parent ExerciseData object.
     * @param sets The list of SetData objects performed in this session.
     */
    suspend fun insertExerciseWithSets(exerciseData: ExerciseData, sets: List<SetData>) {
        exerciseDao.insertExerciseWithSets(exerciseData, sets)
    }
}