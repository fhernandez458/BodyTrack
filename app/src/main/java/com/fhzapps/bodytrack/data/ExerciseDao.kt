package com.fhzapps.bodytrack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.fhzapps.bodytrack.BodyParts.MuscleGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    // Use @Transaction to ensure this happens atomically (all or nothing)
    @Transaction
    @Query("SELECT * FROM exercise_data_table WHERE id = :exerciseDataId")
    fun getExerciseDataWithSets(exerciseDataId: Long): Flow<ExerciseDataWithSets>


    /**
     * Gets all exercises performed on a specific day.
     * You must provide the start and end timestamps for that day.
     * For example, to get all exercises for today, you would pass in
     * today's date at 00:00:00 (startTime) and 23:59:59 (endTime).
     */
    @Transaction
    @Query("SELECT * FROM exercise_data_table WHERE id = :exerciseDataId AND timestamp BETWEEN :startTime AND :endTime")
    fun getExercisesByDate(exerciseDataId: Long, startTime: Long, endTime: Long): Flow<List<ExerciseDataWithSets>>

    // Inserting requires separate calls, often wrapped in a transaction
    @Insert
    suspend fun insertExerciseData(exerciseData: ExerciseData): Long // Returns the new ID

    @Insert
    suspend fun insertSets(sets: List<SetData>)

    // A helper function to handle the whole process
    @Transaction
    suspend fun insertExerciseWithSets(exerciseData: ExerciseData, sets: List<SetData>) {
        // 1. Insert the parent ExerciseData and get its generated ID
        val exerciseDataId = insertExerciseData(exerciseData)

        // 2. Update each SetData object with the correct parent ID
        val setsWithCorrectId = sets.map { it.copy(exerciseDataId = exerciseDataId) }

        // 3. Insert all the sets into the database
        insertSets(setsWithCorrectId)
    }
}