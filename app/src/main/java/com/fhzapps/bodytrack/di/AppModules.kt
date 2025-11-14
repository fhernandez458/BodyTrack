package com.fhzapps.bodytrack.di

import com.fhzapps.bodytrack.BodyPage.BodyPageViewmodel
import com.fhzapps.bodytrack.data.ExerciseRepository
import com.fhzapps.bodytrack.data.ExerciseRepositoryImpl
import com.fhzapps.bodytrack.exercises.ExerciseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModules = module {
    // Define how to create the repository
    single<ExerciseRepository> {
        ExerciseRepositoryImpl(
            exerciseApi = get(),
            exerciseDao = get()
        )
    }

    // ViewModels
    viewModelOf(::ExerciseViewModel)
    viewModelOf(::BodyPageViewmodel)
}
