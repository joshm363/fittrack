package com.fittrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fittrack.app.data.local.entity.ExerciseSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSetDao {
    @Query("SELECT * FROM exercise_sets WHERE sessionExerciseId = :sessionExerciseId ORDER BY setNumber ASC")
    fun getBySessionExerciseId(sessionExerciseId: Long): Flow<List<ExerciseSetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ExerciseSetEntity): Long

    @Update
    suspend fun update(entity: ExerciseSetEntity)

    @Query("DELETE FROM exercise_sets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
