package com.fittrack.app.domain.model

data class Exercise(
    val id: Long = 0,
    val name: String,
    val muscleGroup: MuscleGroup,
    val equipment: Equipment,
    val isCustom: Boolean = false
)
