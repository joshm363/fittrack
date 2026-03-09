package com.fittrack.app.domain.model

data class SessionExercise(
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Long,
    val sortOrder: Int,
    val exercise: Exercise? = null
)
