package com.fhzapps.bodytrack.data

import com.fhzapps.bodytrack.data.workout.SessionExerciseEntity
import com.fhzapps.bodytrack.data.workout.SessionSetEntity
import com.fhzapps.bodytrack.data.workout.WorkoutDao
import com.fhzapps.bodytrack.data.workout.WorkoutEntity
import com.fhzapps.bodytrack.data.workout.WorkoutExerciseEntity
import com.fhzapps.bodytrack.data.workout.WorkoutSessionEntity
import com.fhzapps.bodytrack.data.workout.WorkoutSessionWithDetails
import com.fhzapps.bodytrack.data.workout.WorkoutWithExerciseDetails
import com.fhzapps.bodytrack.data.workout.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    // Workouts
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>
    fun getWorkoutWithExercises(workoutId: Long): Flow<WorkoutWithExercises?>
    suspend fun getWorkoutWithExercisesOnce(workoutId: Long): WorkoutWithExercises?
    fun getWorkoutWithExerciseDetails(workoutId: Long): Flow<WorkoutWithExerciseDetails?>
    suspend fun getWorkoutWithExerciseDetailsOnce(workoutId: Long): WorkoutWithExerciseDetails?
    suspend fun createWorkout(name: String, description: String): Long
    suspend fun updateWorkout(workout: WorkoutEntity)
    suspend fun deleteWorkout(workoutId: Long)
    suspend fun setWorkoutExercises(workoutId: Long, exerciseIds: List<String>)

    // Sessions
    suspend fun startSession(workoutId: Long): Long
    fun getSessionWithDetails(sessionId: Long): Flow<WorkoutSessionWithDetails?>
    suspend fun getSessionWithDetailsOnce(sessionId: Long): WorkoutSessionWithDetails?
    suspend fun completeSession(sessionId: Long, notes: String = "")
    suspend fun deleteSession(sessionId: Long)
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>
    fun getRecentSessions(limit: Int = 20): Flow<List<WorkoutSessionEntity>>

    // Session exercises
    suspend fun updateSessionExercise(exercise: SessionExerciseEntity)

    // Set logging
    suspend fun logSet(
        sessionExerciseId: Long,
        setNumber: Int,
        weight: Double,
        reps: Int,
        unit: WeightUnit,
    ): Long
    suspend fun updateSet(set: SessionSetEntity)
    suspend fun deleteSet(setId: Long)
    fun getSetsForExercise(sessionExerciseId: Long): Flow<List<SessionSetEntity>>
}

class WorkoutRepositoryImpl(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
) : WorkoutRepository {

    // --- Workouts ---

    override fun getAllWorkouts(): Flow<List<WorkoutEntity>> =
        workoutDao.getAllWorkouts()

    override fun getWorkoutWithExercises(workoutId: Long): Flow<WorkoutWithExercises?> =
        workoutDao.getWorkoutWithExercises(workoutId)

    override suspend fun getWorkoutWithExercisesOnce(workoutId: Long): WorkoutWithExercises? =
        workoutDao.getWorkoutWithExercisesOnce(workoutId)

    override fun getWorkoutWithExerciseDetails(workoutId: Long): Flow<WorkoutWithExerciseDetails?> =
        workoutDao.getWorkoutWithExerciseDetails(workoutId)

    override suspend fun getWorkoutWithExerciseDetailsOnce(workoutId: Long): WorkoutWithExerciseDetails? =
        workoutDao.getWorkoutWithExerciseDetailsOnce(workoutId)

    override suspend fun createWorkout(name: String, description: String): Long {
        val workout = WorkoutEntity(
            name = name,
            description = description,
        )
        return workoutDao.insertWorkout(workout)
    }

    override suspend fun updateWorkout(workout: WorkoutEntity) {
        workoutDao.updateWorkout(workout.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteWorkout(workoutId: Long) {
        workoutDao.deleteWorkout(workoutId)
    }

    override suspend fun setWorkoutExercises(workoutId: Long, exerciseIds: List<String>) {
        val workoutExercises = exerciseIds.mapIndexed { index, exerciseId ->
            WorkoutExerciseEntity(
                workoutId = workoutId,
                exerciseId = exerciseId,
                orderIndex = index,
            )
        }
        workoutDao.updateWorkoutExercises(workoutId, workoutExercises)
    }

    // --- Sessions ---

    override suspend fun startSession(workoutId: Long): Long {
        val workout = workoutDao.getWorkoutWithExercisesOnce(workoutId)
            ?: throw IllegalArgumentException("Workout not found: $workoutId")

        val exercises = workout.exercises.map { movement ->
            movement.exerciseId to movement.name
        }

        return workoutDao.startSessionFromWorkout(
            workoutId = workoutId,
            workoutName = workout.workout.name,
            exercises = exercises,
        )
    }

    override fun getSessionWithDetails(sessionId: Long): Flow<WorkoutSessionWithDetails?> =
        workoutDao.getSessionWithDetails(sessionId)

    override suspend fun getSessionWithDetailsOnce(sessionId: Long): WorkoutSessionWithDetails? =
        workoutDao.getSessionWithDetailsOnce(sessionId)

    override suspend fun completeSession(sessionId: Long, notes: String) {
        workoutDao.completeSession(sessionId, notes)
    }

    override suspend fun deleteSession(sessionId: Long) {
        workoutDao.deleteSession(sessionId)
    }

    override fun getAllSessions(): Flow<List<WorkoutSessionEntity>> =
        workoutDao.getAllSessions()

    override fun getRecentSessions(limit: Int): Flow<List<WorkoutSessionEntity>> =
        workoutDao.getRecentSessions(limit)

    // --- Session Exercises ---

    override suspend fun updateSessionExercise(exercise: SessionExerciseEntity) {
        workoutDao.updateSessionExercise(exercise)
    }

    // --- Sets ---

    override suspend fun logSet(
        sessionExerciseId: Long,
        setNumber: Int,
        weight: Double,
        reps: Int,
        unit: WeightUnit,
    ): Long {
        val set = SessionSetEntity(
            sessionExerciseId = sessionExerciseId,
            setNumber = setNumber,
            weight = weight,
            reps = reps,
            unit = unit,
            isCompleted = true,
        )
        return workoutDao.insertSet(set)
    }

    override suspend fun updateSet(set: SessionSetEntity) {
        workoutDao.updateSet(set)
    }

    override suspend fun deleteSet(setId: Long) {
        workoutDao.deleteSet(setId)
    }

    override fun getSetsForExercise(sessionExerciseId: Long): Flow<List<SessionSetEntity>> =
        workoutDao.getSetsForExercise(sessionExerciseId)
}
