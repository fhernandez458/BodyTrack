package com.fhzapps.bodytrack.data.workout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // --- Workout CRUD ---

    @Query("SELECT * FROM workout_table ORDER BY updatedAt DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    fun getWorkoutWithExercises(workoutId: Long): Flow<WorkoutWithExercises?>

    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    suspend fun getWorkoutWithExercisesOnce(workoutId: Long): WorkoutWithExercises?

    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    fun getWorkoutWithExerciseDetails(workoutId: Long): Flow<WorkoutWithExerciseDetails?>

    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    suspend fun getWorkoutWithExerciseDetailsOnce(workoutId: Long): WorkoutWithExerciseDetails?

    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Query("DELETE FROM workout_table WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: Long)

    // --- Workout Exercises ---

    @Query("SELECT * FROM workout_exercise_table WHERE workoutId = :workoutId ORDER BY orderIndex ASC")
    suspend fun getWorkoutExercises(workoutId: Long): List<WorkoutExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercises(exercises: List<WorkoutExerciseEntity>)

    @Query("DELETE FROM workout_exercise_table WHERE workoutId = :workoutId")
    suspend fun deleteWorkoutExercises(workoutId: Long)

    @Transaction
    suspend fun updateWorkoutExercises(workoutId: Long, exercises: List<WorkoutExerciseEntity>) {
        deleteWorkoutExercises(workoutId)
        insertWorkoutExercises(exercises)
    }

    // --- Sessions ---

    @Insert
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_session_table WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): WorkoutSessionEntity?

    @Transaction
    @Query("SELECT * FROM workout_session_table WHERE id = :sessionId")
    fun getSessionWithDetails(sessionId: Long): Flow<WorkoutSessionWithDetails?>

    @Transaction
    @Query("SELECT * FROM workout_session_table WHERE id = :sessionId")
    suspend fun getSessionWithDetailsOnce(sessionId: Long): WorkoutSessionWithDetails?

    @Query("SELECT * FROM workout_session_table ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_session_table ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 20): Flow<List<WorkoutSessionEntity>>

    @Query("DELETE FROM workout_session_table WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    // --- Session Exercises ---

    @Insert
    suspend fun insertSessionExercise(exercise: SessionExerciseEntity): Long

    @Insert
    suspend fun insertSessionExercises(exercises: List<SessionExerciseEntity>): List<Long>

    @Update
    suspend fun updateSessionExercise(exercise: SessionExerciseEntity)

    @Query("SELECT * FROM session_exercise_table WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    suspend fun getSessionExercises(sessionId: Long): List<SessionExerciseEntity>

    // --- Sets ---

    @Insert
    suspend fun insertSet(set: SessionSetEntity): Long

    @Update
    suspend fun updateSet(set: SessionSetEntity)

    @Query("DELETE FROM session_set_table WHERE id = :setId")
    suspend fun deleteSet(setId: Long)

    @Query("SELECT * FROM session_set_table WHERE sessionExerciseId = :sessionExerciseId ORDER BY setNumber ASC")
    fun getSetsForExercise(sessionExerciseId: Long): Flow<List<SessionSetEntity>>

    // --- Transaction: Start a new session from workout ---

    @Transaction
    suspend fun startSessionFromWorkout(workoutId: Long, workoutName: String, exercises: List<Pair<String, String>>): Long {
        val session = WorkoutSessionEntity(
            workoutId = workoutId,
            workoutName = workoutName,
        )
        val sessionId = insertSession(session)

        val sessionExercises = exercises.mapIndexed { index, (exerciseId, exerciseName) ->
            SessionExerciseEntity(
                sessionId = sessionId,
                exerciseId = exerciseId,
                exerciseName = exerciseName,
                orderIndex = index,
            )
        }
        insertSessionExercises(sessionExercises)

        return sessionId
    }

    @Transaction
    suspend fun completeSession(sessionId: Long, notes: String = "") {
        val session = getSessionById(sessionId) ?: return
        updateSession(
            session.copy(
                endTime = System.currentTimeMillis(),
                isCompleted = true,
                notes = notes,
            )
        )
    }
}
