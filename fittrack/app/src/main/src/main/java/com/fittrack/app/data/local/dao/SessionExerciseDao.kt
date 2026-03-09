package com.fittrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fittrack.app.data.local.entity.SessionExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionExerciseDao {
    @Query("SELECT * FROM session_exercises WHERE sessionId = :sessionId ORDER BY sortOrder ASC")
    fun getBySessionId(sessionId: Long): Flow<List<SessionExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SessionExerciseEntity): Long

    @Query("DELETE FROM session_exercises WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("""
        SELECT DISTINCT e.muscleGroup
        FROM session_exercises se
        JOIN exercises e ON se.exerciseId = e.id
        WHERE se.sessionId = :sessionId
    """)
    suspend fun getMuscleGroupsForSession(sessionId: Long): List<String>
}
