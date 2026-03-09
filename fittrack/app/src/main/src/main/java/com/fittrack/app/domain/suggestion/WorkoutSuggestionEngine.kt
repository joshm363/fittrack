package com.fittrack.app.domain.suggestion

import com.fittrack.app.data.local.UserPreferencesManager
import com.fittrack.app.domain.model.Equipment
import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.MuscleGroup
import com.fittrack.app.domain.model.SplitDay
import com.fittrack.app.domain.model.WorkoutStrategy
import com.fittrack.app.domain.repository.ExerciseRepository
import com.fittrack.app.domain.repository.WorkoutRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class WorkoutSuggestion(
    val dayLabel: String,
    val exercises: List<Exercise>
)

@Singleton
class WorkoutSuggestionEngine @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val preferencesManager: UserPreferencesManager
) {

    suspend fun suggest(strategy: WorkoutStrategy): WorkoutSuggestion {
        val splitDay = determineNextSplitDay(strategy)
        val exercises = selectExercises(splitDay, strategy)
        return WorkoutSuggestion(
            dayLabel = splitDay.label,
            exercises = exercises
        )
    }

    private suspend fun determineNextSplitDay(strategy: WorkoutStrategy): SplitDay {
        val days = strategy.getDays()
        if (days.size == 1) return days.first()

        val lastSession = workoutRepository.getLastCompletedSession() ?: return days.first()
        val lastMuscleGroups = workoutRepository
            .getMuscleGroupsForSession(lastSession.id)
            .mapNotNull { name ->
                try { MuscleGroup.valueOf(name) } catch (_: IllegalArgumentException) { null }
            }
            .toSet()

        if (lastMuscleGroups.isEmpty()) return days.first()

        val lastDayIndex = days.indices.maxByOrNull { i ->
            lastMuscleGroups.count { it in days[i].muscleGroups }
        } ?: 0

        return days[(lastDayIndex + 1) % days.size]
    }

    private suspend fun selectExercises(
        splitDay: SplitDay,
        strategy: WorkoutStrategy
    ): List<Exercise> {
        val availableEquipment = preferencesManager.getAvailableEquipment()
        val allExercises = exerciseRepository.getExercisesByMuscleGroups(splitDay.muscleGroups)
            .filter { it.equipment in availableEquipment }
        val countPerGroup = if (strategy == WorkoutStrategy.FULL_BODY) 1 else 2
        val daySeed = LocalDate.now().toEpochDay().toInt()

        return splitDay.muscleGroups.flatMap { group ->
            val groupExercises = allExercises.filter { it.muscleGroup == group }
            if (groupExercises.isEmpty()) return@flatMap emptyList()
            val offset = (daySeed % groupExercises.size).coerceAtLeast(0)
            (0 until countPerGroup.coerceAtMost(groupExercises.size)).map { i ->
                groupExercises[(offset + i) % groupExercises.size]
            }
        }
    }
}
