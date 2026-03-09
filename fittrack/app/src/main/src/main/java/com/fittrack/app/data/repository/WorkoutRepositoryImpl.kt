package com.fittrack.app.data.repository

import com.fittrack.app.data.local.dao.ExerciseDao
import com.fittrack.app.data.local.dao.ExerciseSetDao
import com.fittrack.app.data.local.dao.SessionExerciseDao
import com.fittrack.app.data.local.dao.WorkoutSessionDao
import com.fittrack.app.data.local.entity.ExerciseEntity
import com.fittrack.app.data.local.entity.ExerciseSetEntity
import com.fittrack.app.data.local.entity.SessionExerciseEntity
import com.fittrack.app.data.local.entity.WorkoutSessionEntity
import com.fittrack.app.domain.model.Equipment
import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.MuscleGroup
import com.fittrack.app.domain.model.ExerciseSet
import com.fittrack.app.domain.model.SessionExercise
import com.fittrack.app.domain.model.WorkoutSession
import com.fittrack.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutSessionDao: WorkoutSessionDao,
    private val sessionExerciseDao: SessionExerciseDao,
    private val exerciseSetDao: ExerciseSetDao,
    private val exerciseDao: ExerciseDao
) : WorkoutRepository {

    override fun getAllSessions(): Flow<List<WorkoutSession>> =
        workoutSessionDao.getAll().map { it.map(WorkoutSessionEntity::toDomain) }

    override fun getRecentSessions(limit: Int): Flow<List<WorkoutSession>> =
        workoutSessionDao.getRecent(limit).map { it.map(WorkoutSessionEntity::toDomain) }

    override suspend fun getSessionById(id: Long): WorkoutSession? =
        workoutSessionDao.getById(id)?.toDomain()

    override suspend fun startSession(name: String): Long {
        val entity = WorkoutSessionEntity(
            name = name,
            startTime = System.currentTimeMillis(),
            endTime = null,
            durationSeconds = 0,
            isComplete = false
        )
        return workoutSessionDao.insert(entity)
    }

    override suspend fun completeSession(sessionId: Long) {
        val session = workoutSessionDao.getById(sessionId) ?: return
        val endTime = System.currentTimeMillis()
        val durationSeconds = ((endTime - session.startTime) / 1000).toInt()
        val updated = session.copy(
            endTime = endTime,
            durationSeconds = durationSeconds,
            isComplete = true
        )
        workoutSessionDao.update(updated)
    }

    override suspend fun deleteSession(sessionId: Long) {
        workoutSessionDao.deleteById(sessionId)
    }

    override fun getSessionExercises(sessionId: Long): Flow<List<SessionExercise>> =
        sessionExerciseDao.getBySessionId(sessionId).map { entities ->
            entities.map { entity ->
                val exercise = exerciseDao.getById(entity.exerciseId)?.toDomain()
                entity.toDomain(exercise)
            }
        }

    override suspend fun addExerciseToSession(sessionId: Long, exerciseId: Long, sortOrder: Int): Long {
        val entity = SessionExerciseEntity(
            sessionId = sessionId,
            exerciseId = exerciseId,
            sortOrder = sortOrder
        )
        return sessionExerciseDao.insert(entity)
    }

    override suspend fun removeExerciseFromSession(sessionExerciseId: Long) {
        sessionExerciseDao.deleteById(sessionExerciseId)
    }

    override fun getSetsForSessionExercise(sessionExerciseId: Long): Flow<List<ExerciseSet>> =
        exerciseSetDao.getBySessionExerciseId(sessionExerciseId).map { it.map(ExerciseSetEntity::toDomain) }

    override suspend fun addSet(sessionExerciseId: Long, setNumber: Int, weight: Double, reps: Int): Long {
        val entity = ExerciseSetEntity(
            sessionExerciseId = sessionExerciseId,
            setNumber = setNumber,
            reps = reps,
            weight = weight,
            isComplete = false
        )
        return exerciseSetDao.insert(entity)
    }

    override suspend fun updateSet(set: ExerciseSet) {
        exerciseSetDao.update(set.toEntity())
    }

    override suspend fun deleteSet(setId: Long) {
        exerciseSetDao.deleteById(setId)
    }

    override suspend fun getLastCompletedSession(): WorkoutSession? =
        workoutSessionDao.getLastCompleted()?.toDomain()

    override suspend fun getMuscleGroupsForSession(sessionId: Long): List<String> =
        sessionExerciseDao.getMuscleGroupsForSession(sessionId)

    override suspend fun getPreviousSetsForExercise(exerciseId: Long): List<ExerciseSet> =
        exerciseSetDao.getPreviousSetsForExercise(exerciseId).map(ExerciseSetEntity::toDomain)

    override suspend fun getAllCompletedSetsForExercise(exerciseId: Long): List<ExerciseSet> =
        exerciseSetDao.getAllCompletedSetsForExercise(exerciseId).map(ExerciseSetEntity::toDomain)
}

private fun WorkoutSessionEntity.toDomain(): WorkoutSession = WorkoutSession(
    id = id,
    name = name,
    startTime = startTime,
    endTime = endTime,
    durationSeconds = durationSeconds,
    isComplete = isComplete
)

private fun SessionExerciseEntity.toDomain(exercise: Exercise?): SessionExercise = SessionExercise(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    sortOrder = sortOrder,
    exercise = exercise
)

private fun ExerciseSetEntity.toDomain(): ExerciseSet = ExerciseSet(
    id = id,
    sessionExerciseId = sessionExerciseId,
    setNumber = setNumber,
    reps = reps,
    weight = weight,
    isComplete = isComplete
)

private fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    muscleGroup = MuscleGroup.valueOf(muscleGroup),
    equipment = Equipment.valueOf(equipment),
    isCustom = isCustom
)

private fun ExerciseSet.toEntity(): ExerciseSetEntity = ExerciseSetEntity(
    id = id,
    sessionExerciseId = sessionExerciseId,
    setNumber = setNumber,
    reps = reps,
    weight = weight,
    isComplete = isComplete
)
