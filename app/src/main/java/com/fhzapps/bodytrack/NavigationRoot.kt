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
import com.fhzapps.bodytrack.BodyPage.BodyPageEvent
import com.fhzapps.bodytrack.BodyPage.BodyPageListViewRoot
import com.fhzapps.bodytrack.BodyPage.BodyPageViewmodel
import com.fhzapps.bodytrack.exercises.ExerciseListRoot
import com.fhzapps.bodytrack.exercises.ExercisePageRoot
import org.koin.androidx.compose.koinViewModel

//Shared viewmodel in order to keep track of what bodypart/exercise is selected between screens
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
            Log.d("NavigationRoot", "bodyListView")
            BodyPageListViewRoot(
                onBodyPartClicked = {
                    navController.navigate("exerciseListPage") }
            )

        }

        composable(route = "exerciseListPage") { //navigate to a list of exercises for the given bodypart
            Log.d("NavigationRoot", "exerciseListPage")
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