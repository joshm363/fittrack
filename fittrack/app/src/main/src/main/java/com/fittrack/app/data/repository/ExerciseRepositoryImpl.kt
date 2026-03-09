package com.fittrack.app.data.repository

import com.fittrack.app.data.local.SeedData
import com.fittrack.app.data.local.dao.ExerciseDao
import com.fittrack.app.data.local.entity.ExerciseEntity
import com.fittrack.app.domain.model.Equipment
import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.MuscleGroup
import com.fittrack.app.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao.getAll().map { it.map(ExerciseEntity::toDomain) }

    override fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>> =
        exerciseDao.getByMuscleGroup(muscleGroup.name).map { it.map(ExerciseEntity::toDomain) }

    override fun searchExercises(query: String): Flow<List<Exercise>> =
        exerciseDao.search(query).map { it.map(ExerciseEntity::toDomain) }

    override suspend fun getExerciseById(id: Long): Exercise? =
        exerciseDao.getById(id)?.toDomain()

    override suspend fun insertExercise(exercise: Exercise): Long =
        exerciseDao.insert(exercise.toEntity())

    override suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.delete(exercise.toEntity())
    }

    override suspend fun seedExercisesIfEmpty() {
        if (exerciseDao.getCount() == 0) {
            SeedData.getDefaultExercises().forEach { exerciseDao.insert(it) }
        }
    }

    override suspend fun getExercisesByMuscleGroups(muscleGroups: List<MuscleGroup>): List<Exercise> =
        exerciseDao.getByMuscleGroups(muscleGroups.map { it.name }).map(ExerciseEntity::toDomain)
}

private fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    muscleGroup = MuscleGroup.valueOf(muscleGroup),
    equipment = Equipment.valueOf(equipment),
    isCustom = isCustom
)

private fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(
    id = id,
    name = name,
    muscleGroup = muscleGroup.name,
    equipment = equipment.name,
    isCustom = isCustom
)
