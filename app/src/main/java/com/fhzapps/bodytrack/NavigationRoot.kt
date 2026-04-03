package com.fhzapps.bodytrack

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fhzapps.bodytrack.BodyPage.BodyPageListViewRoot
import com.fhzapps.bodytrack.exercises.ExerciseListRoot
import com.fhzapps.bodytrack.exercises.ExercisePageRoot
import com.fhzapps.bodytrack.workout.create.CreateWorkoutRoot
import com.fhzapps.bodytrack.workout.history.WorkoutHistoryRoot
import com.fhzapps.bodytrack.workout.list.WorkoutListRoot
import com.fhzapps.bodytrack.workout.picker.ExercisePickerRoot
import com.fhzapps.bodytrack.workout.session.ActiveSessionRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = "workouts"
    ) {
        workoutsGraph(navController)
        exercisesGraph(navController)
    }
}

private fun NavGraphBuilder.workoutsGraph(navController: NavHostController) {
    navigation(
        startDestination = "workoutList",
        route = "workouts"
    ) {
        composable(route = "workoutList") {
            WorkoutListRoot(
                onNavigateToCreate = {
                    navController.navigate("createWorkout")
                },
                onNavigateToEdit = { workoutId ->
                    navController.navigate("editWorkout/$workoutId")
                },
                onNavigateToSession = { sessionId ->
                    navController.navigate("activeSession/$sessionId")
                },
                onNavigateToHistory = {
                    navController.navigate("workoutHistory")
                },
                onNavigateToExercises = {
                    navController.navigate("exercises")
                },
            )
        }

        composable(route = "createWorkout") { backStackEntry ->
            val selectedExercises = backStackEntry.savedStateHandle
                .getStateFlow<List<String>?>("selectedExercises", null)
                .collectAsStateWithLifecycle()

            CreateWorkoutRoot(
                workoutId = null,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToExercisePicker = {
                    navController.navigate("exercisePicker")
                },
                selectedExerciseIds = selectedExercises.value,
            )
        }

        composable(route = "editWorkout/{workoutId}") { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId")?.toLongOrNull()
            val selectedExercises = backStackEntry.savedStateHandle
                .getStateFlow<List<String>?>("selectedExercises", null)
                .collectAsStateWithLifecycle()

            CreateWorkoutRoot(
                workoutId = workoutId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToExercisePicker = {
                    navController.navigate("exercisePicker")
                },
                selectedExerciseIds = selectedExercises.value,
            )
        }

        composable(route = "exercisePicker") {
            ExercisePickerRoot(
                onExercisesSelected = { selectedIds ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "selectedExercises",
                        selectedIds
                    )
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(route = "activeSession/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLongOrNull() ?: 0L
            ActiveSessionRoot(
                sessionId = sessionId,
                onSessionComplete = {
                    navController.popBackStack("workoutList", inclusive = false)
                },
                onNavigateBack = {
                    navController.popBackStack("workoutList", inclusive = false)
                },
            )
        }

        composable(route = "workoutHistory") {
            WorkoutHistoryRoot(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

private fun NavGraphBuilder.exercisesGraph(navController: NavHostController) {
    navigation(
        startDestination = "bodyListView",
        route = "exercises"
    ) {
        composable(route = "bodyListView") {
            BodyPageListViewRoot(
                onBodyPartClicked = { bodyPart ->
                    navController.navigate("exerciseListPage/${bodyPart.muscleGroup.name}")
                }
            )
        }

        composable(route = "exerciseListPage/{bodyPart}") { backStackEntry ->
            val bodyPart = backStackEntry.arguments?.getString("bodyPart") ?: ""
            ExerciseListRoot(
                bodyPart = bodyPart,
                onExerciseClicked = { exerciseId ->
                    Log.d("NavigationRoot", "onExerciseListItemClicked: $exerciseId")
                    navController.navigate("exercisePage/$exerciseId")
                },
            )
        }

        composable(route = "exercisePage/{exerciseId}") { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
            ExercisePageRoot(exerciseId = exerciseId)
        }
    }
}
