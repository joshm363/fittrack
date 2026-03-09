package com.fittrack.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fittrack.app.data.local.dao.ExerciseDao
import com.fittrack.app.data.local.dao.ExerciseSetDao
import com.fittrack.app.data.local.dao.SessionExerciseDao
import com.fittrack.app.data.local.dao.WorkoutSessionDao
import com.fittrack.app.data.local.entity.ExerciseEntity
import com.fittrack.app.data.local.entity.ExerciseSetEntity
import com.fittrack.app.data.local.entity.SessionExerciseEntity
import com.fittrack.app.data.local.entity.WorkoutSessionEntity

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        SessionExerciseEntity::class,
        ExerciseSetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun sessionExerciseDao(): SessionExerciseDao
    abstract fun exerciseSetDao(): ExerciseSetDao

    companion object {
        const val DATABASE_NAME = "fittrack_db"
    }
}
