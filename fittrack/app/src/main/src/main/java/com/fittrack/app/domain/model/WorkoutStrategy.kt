package com.fittrack.app.domain.model

data class SplitDay(
    val label: String,
    val muscleGroups: List<MuscleGroup>
)

enum class WorkoutStrategy(val displayName: String) {
    FULL_BODY("Full Body"),
    UPPER_LOWER("Upper / Lower"),
    PUSH_PULL_LEGS("Push / Pull / Legs");

    fun getDays(): List<SplitDay> = when (this) {
        FULL_BODY -> listOf(
            SplitDay("Full Body", listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.SHOULDERS,
                MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS, MuscleGroup.CORE
            ))
        )
        UPPER_LOWER -> listOf(
            SplitDay("Upper Body", listOf(
                MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.SHOULDERS,
                MuscleGroup.BICEPS, MuscleGroup.TRICEPS
            )),
            SplitDay("Lower Body", listOf(
                MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES,
                MuscleGroup.CALVES, MuscleGroup.CORE
            ))
        )
        PUSH_PULL_LEGS -> listOf(
            SplitDay("Push", listOf(
                MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS
            )),
            SplitDay("Pull", listOf(
                MuscleGroup.BACK, MuscleGroup.BICEPS
            )),
            SplitDay("Legs", listOf(
                MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES,
                MuscleGroup.CALVES, MuscleGroup.CORE
            ))
        )
    }
}
