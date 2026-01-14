package com.fhzapps.bodytrack

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fhzapps.bodytrack.BodyPage.BodyPageListViewRoot
import com.fhzapps.bodytrack.exercises.ExerciseListRoot
import com.fhzapps.bodytrack.exercises.ExercisePageRoot
import org.koin.androidx.compose.koinViewModel

@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
    graphRoute: String
): T {
    val navGraphRouteEntry = remember(this){
        navController.getBackStackEntry(graphRoute)
    }
    return koinViewModel(viewModelStoreOwner = navGraphRouteEntry)
}

@Composable
fun NavigationRoot (
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ){
        homeGraph(navController)
    }
}
private fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    navigation(
        startDestination = "bodyListView",
        route = "home"
    ) {
        composable(route = "bodyListView") { entry ->
            BodyPageListViewRoot(
                onBodyPartClicked = { bodyPart ->
                    navController.navigate("exerciseListPage/${bodyPart.muscleGroup.name}") }
            )

        }

        composable(route = "exerciseListPage/{bodyPart}") { backStackEntry ->
            //navigate to a list of exercises for the given bodypart
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