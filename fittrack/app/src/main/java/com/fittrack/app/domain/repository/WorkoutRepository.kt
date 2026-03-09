package com.fittrack.app.domain.repository

import com.fittrack.app.domain.model.ExerciseSet
import com.fittrack.app.domain.model.SessionExercise
import com.fittrack.app.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllSessions(): Flow<List<WorkoutSession>>
    fun getRecentSessions(limit: Int): Flow<List<WorkoutSession>>
    suspend fun getSessionById(id: Long): WorkoutSession?
    suspend fun startSession(name: String): Long
    suspend fun completeSession(sessionId: Long)
    suspend fun deleteSession(sessionId: Long)
    fun getSessionExercises(sessionId: Long): Flow<List<SessionExercise>>
    suspend fun addExerciseToSession(sessionId: Long, exerciseId: Long, sortOrder: Int): Long
    suspend fun removeExerciseFromSession(sessionExerciseId: Long)
    fun getSetsForSessionExercise(sessionExerciseId: Long): Flow<List<ExerciseSet>>
    suspend fun addSet(sessionExerciseId: Long, setNumber: Int): Long
    suspend fun updateSet(set: ExerciseSet)
    suspend fun deleteSet(setId: Long)
}
