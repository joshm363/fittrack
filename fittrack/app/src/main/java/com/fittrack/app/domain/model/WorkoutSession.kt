package com.fittrack.app.domain.model

data class WorkoutSession(
    val id: Long = 0,
    val name: String,
    val startTime: Long,
    val endTime: Long? = null,
    val durationSeconds: Int = 0,
    val isComplete: Boolean = false
)
