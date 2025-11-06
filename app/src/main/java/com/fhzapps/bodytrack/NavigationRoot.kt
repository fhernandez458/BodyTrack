package com.fhzapps.bodytrack

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fhzapps.bodytrack.BodyPage.BodyPageListViewRoot
import com.fhzapps.bodytrack.BodyPage.BodyPageViewmodel
import com.fhzapps.bodytrack.exercises.ExerciseListRoot
import com.fhzapps.bodytrack.exercises.ExercisePageRoot
import org.koin.androidx.compose.koinViewModel

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
        composable(route = "bodyListView") {
            Log.d("NavigationRoot", "bodyListView")
            // Find the back stack entry for the parent graph ('home')
            val parentEntry = remember(it) { navController.getBackStackEntry("home") }
            // Scope the ViewModel to the graph's lifecycle
            val bodyListViewModel: BodyPageViewmodel = koinViewModel(viewModelStoreOwner = parentEntry)
            BodyPageListViewRoot(
                onBodyPartClicked = { navController.navigate("exerciseListPage") }
            )

        }

        composable(route = "exerciseListPage") { //navigate to a list of exercises for the given bodypart
            Log.d("NavigationRoot", "exerciseListPage")
            val parentEntry = remember(it) { navController.getBackStackEntry("bodyListView") }
            val bodyListViewModel: BodyPageViewmodel = koinViewModel(viewModelStoreOwner = parentEntry)
            ExerciseListRoot(
                onExerciseClicked = {
                    Log.d("NavigationRoot", "onExerciseClicked")
                    navController.navigate("exercisePage") },
            )
        }

        composable(route = "exercisePage") { // shows a single exercise
            Log.d("NavigationRoot", "exercisePage")
            val parentEntry = remember(it) { navController.getBackStackEntry("exerciseListPage") }
            ExercisePageRoot()
        }
    }
}