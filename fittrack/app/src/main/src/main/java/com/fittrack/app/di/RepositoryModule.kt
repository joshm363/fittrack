package com.fittrack.app.di

import com.fittrack.app.data.repository.ExerciseRepositoryImpl
import com.fittrack.app.data.repository.WorkoutRepositoryImpl
import com.fittrack.app.domain.repository.ExerciseRepository
import com.fittrack.app.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        impl: ExerciseRepositoryImpl
    ): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        impl: WorkoutRepositoryImpl
    ): WorkoutRepository
}
