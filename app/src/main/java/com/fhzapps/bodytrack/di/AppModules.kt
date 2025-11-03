package com.fhzapps.bodytrack.di

import androidx.room.Room
import com.fhzapps.bodytrack.BodyPage.BodyPageViewmodel
import com.fhzapps.bodytrack.data.AppDatabase
import com.fhzapps.bodytrack.data.ExerciseRepository
import com.fhzapps.bodytrack.exercises.ExerciseViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModules = module {
    // Define how to create a single instance of your Room database
   single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "bodytrack_database"
        ).build()
    }

    // Define how to get the DAO from the database
    single { get<AppDatabase>().exerciseDao() }

    // Define how to create the repository
    single { ExerciseRepository(get()) }

//    // Define your ViewModels
    viewModelOf(::ExerciseViewModel)
    viewModelOf(::BodyPageViewmodel)
}