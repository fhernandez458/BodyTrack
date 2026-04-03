package com.fhzapps.bodytrack.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fhzapps.bodytrack.data.workout.SessionExerciseEntity
import com.fhzapps.bodytrack.data.workout.SessionSetEntity
import com.fhzapps.bodytrack.data.workout.WorkoutDao
import com.fhzapps.bodytrack.data.workout.WorkoutEntity
import com.fhzapps.bodytrack.data.workout.WorkoutExerciseEntity
import com.fhzapps.bodytrack.data.workout.WorkoutSessionEntity

@Database(
    entities = [
        ExerciseData::class,
        SetData::class,
        MovementEntity::class,
        WorkoutEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSessionEntity::class,
        SessionExerciseEntity::class,
        SessionSetEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao

    abstract fun workoutDao(): WorkoutDao
}
