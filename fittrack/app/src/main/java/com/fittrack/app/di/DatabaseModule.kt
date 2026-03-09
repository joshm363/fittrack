package com.fittrack.app.di

import android.content.Context
import androidx.room.Room
import com.fittrack.app.data.local.FitTrackDatabase
import com.fittrack.app.data.local.dao.ExerciseDao
import com.fittrack.app.data.local.dao.ExerciseSetDao
import com.fittrack.app.data.local.dao.SessionExerciseDao
import com.fittrack.app.data.local.dao.WorkoutSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFitTrackDatabase(
        @ApplicationContext context: Context
    ): FitTrackDatabase = Room.databaseBuilder(
        context,
        FitTrackDatabase::class.java,
        FitTrackDatabase.DATABASE_NAME
    ).build()

    @Provides
    fun provideExerciseDao(database: FitTrackDatabase): ExerciseDao =
        database.exerciseDao()

    @Provides
    fun provideWorkoutSessionDao(database: FitTrackDatabase): WorkoutSessionDao =
        database.workoutSessionDao()

    @Provides
    fun provideSessionExerciseDao(database: FitTrackDatabase): SessionExerciseDao =
        database.sessionExerciseDao()

    @Provides
    fun provideExerciseSetDao(database: FitTrackDatabase): ExerciseSetDao =
        database.exerciseSetDao()
}
