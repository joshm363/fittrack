package com.fittrack.app.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ExerciseLibrary : Screen("exercises")
    data object ActiveWorkout : Screen("workout/{sessionId}") {
        fun createRoute(sessionId: Long) = "workout/$sessionId"
    }
    data object History : Screen("history")
}
