package com.fhzapps.bodytrack.di

import android.app.Application
import androidx.room.Room
import com.fhzapps.bodytrack.data.AppDatabase
import com.fhzapps.bodytrack.data.ExerciseDao
import org.koin.dsl.module

fun provideDatabase(application: Application) : AppDatabase =
    Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "body_track_database"
    ).fallbackToDestructiveMigration().build()

fun provideDao (database: AppDatabase) : ExerciseDao = database.exerciseDao()

val databaseModule = module {
    single { provideDatabase(get()) }
    single { provideDao(get()) }
}
