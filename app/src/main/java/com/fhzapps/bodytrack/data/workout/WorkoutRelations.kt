package com.fhzapps.bodytrack.data.workout

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fhzapps.bodytrack.data.MovementEntity

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId",
        associateBy = Junction(
            value = WorkoutExerciseEntity::class,
            parentColumn = "workoutId",
            entityColumn = "exerciseId",
        ),
    )
    val exercises: List<MovementEntity>,
)

data class WorkoutExerciseWithDetails(
    @Embedded val workoutExercise: WorkoutExerciseEntity,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "exerciseId",
    )
    val movement: MovementEntity,
)

data class WorkoutWithExerciseDetails(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId",
        entity = WorkoutExerciseEntity::class,
    )
    val workoutExercises: List<WorkoutExerciseWithDetails>,
)

data class SessionExerciseWithSets(
    @Embedded val sessionExercise: SessionExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionExerciseId",
    )
    val sets: List<SessionSetEntity>,
)

data class WorkoutSessionWithDetails(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId",
        entity = SessionExerciseEntity::class,
    )
    val exercises: List<SessionExerciseWithSets>,
)
