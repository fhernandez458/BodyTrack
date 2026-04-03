package com.fhzapps.bodytrack.di

import com.fhzapps.bodytrack.BodyPage.BodyPageViewmodel
import com.fhzapps.bodytrack.data.ExerciseRepository
import com.fhzapps.bodytrack.data.ExerciseRepositoryImpl
import com.fhzapps.bodytrack.data.WorkoutRepository
import com.fhzapps.bodytrack.data.WorkoutRepositoryImpl
import com.fhzapps.bodytrack.exercises.ExerciseViewModel
import com.fhzapps.bodytrack.workout.create.CreateWorkoutViewModel
import com.fhzapps.bodytrack.workout.history.WorkoutHistoryViewModel
import com.fhzapps.bodytrack.workout.list.WorkoutListViewModel
import com.fhzapps.bodytrack.workout.picker.ExercisePickerViewModel
import com.fhzapps.bodytrack.workout.session.ActiveSessionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModules = module {
    // Repositories
    single<ExerciseRepository> {
        ExerciseRepositoryImpl(
            exerciseDao = get(),
        )
    }
    single<WorkoutRepository> {
        WorkoutRepositoryImpl(
            workoutDao = get(),
            exerciseDao = get(),
        )
    }

    // ViewModels
    viewModelOf(::ExerciseViewModel)
    viewModelOf(::BodyPageViewmodel)
    viewModelOf(::WorkoutListViewModel)
    viewModelOf(::CreateWorkoutViewModel)
    viewModelOf(::ExercisePickerViewModel)
    viewModelOf(::ActiveSessionViewModel)
    viewModelOf(::WorkoutHistoryViewModel)
}
