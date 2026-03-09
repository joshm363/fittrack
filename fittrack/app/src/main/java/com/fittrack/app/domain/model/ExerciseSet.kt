package com.fittrack.app.domain.model

data class ExerciseSet(
    val id: Long = 0,
    val sessionExerciseId: Long,
    val setNumber: Int,
    val reps: Int = 0,
    val weight: Double = 0.0,
    val isComplete: Boolean = false
)
