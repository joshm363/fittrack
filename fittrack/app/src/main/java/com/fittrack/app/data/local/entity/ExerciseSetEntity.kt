package com.fittrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = SessionExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionExerciseId")]
)
data class ExerciseSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionExerciseId: Long,
    val setNumber: Int,
    val reps: Int = 0,
    val weight: Double = 0.0,
    val isComplete: Boolean = false
)
