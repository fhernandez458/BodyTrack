package com.fhzapps.bodytrack.di

import android.app.Application
import androidx.room.Room
import com.fhzapps.bodytrack.data.AppDatabase
import com.fhzapps.bodytrack.data.ExerciseDao
import com.fhzapps.bodytrack.data.workout.WorkoutDao
import org.koin.dsl.module

fun provideDatabase(application: Application): AppDatabase =
    Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "body_track_database"
    )
        .createFromAsset("body_track.db")
        .fallbackToDestructiveMigration()
        .build()

fun provideExerciseDao(database: AppDatabase): ExerciseDao = database.exerciseDao()

fun provideWorkoutDao(database: AppDatabase): WorkoutDao = database.workoutDao()

val databaseModule = module {
    single { provideDatabase(get()) }
    single { provideExerciseDao(get()) }
    single { provideWorkoutDao(get()) }
}
