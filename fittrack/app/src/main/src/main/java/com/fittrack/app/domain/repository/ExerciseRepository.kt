package com.fittrack.app.domain.repository

import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>>
    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>>
    fun searchExercises(query: String): Flow<List<Exercise>>
    suspend fun getExerciseById(id: Long): Exercise?
    suspend fun insertExercise(exercise: Exercise): Long
    suspend fun deleteExercise(exercise: Exercise)
    suspend fun seedExercisesIfEmpty()
    suspend fun getExercisesByMuscleGroups(muscleGroups: List<MuscleGroup>): List<Exercise>
}
