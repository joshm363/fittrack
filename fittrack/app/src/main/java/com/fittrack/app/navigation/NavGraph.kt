package com.fittrack.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fittrack.app.ui.exercises.ExerciseLibraryScreen
import com.fittrack.app.ui.history.HistoryScreen
import com.fittrack.app.ui.home.HomeScreen
import com.fittrack.app.ui.workout.ActiveWorkoutScreen

@Composable
fun FitTrackNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartWorkout = { sessionId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(sessionId))
                }
            )
        }
        composable(Screen.ExerciseLibrary.route) {
            ExerciseLibraryScreen()
        }
        composable(
            route = Screen.ActiveWorkout.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) {
            ActiveWorkoutScreen(
                onWorkoutComplete = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
    }
}
