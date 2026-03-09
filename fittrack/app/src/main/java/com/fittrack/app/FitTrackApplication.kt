package com.fittrack.app

import android.app.Application
import com.fittrack.app.domain.repository.ExerciseRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FitTrackApplication : Application() {

    @Inject
    lateinit var exerciseRepository: ExerciseRepository

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            exerciseRepository.seedExercisesIfEmpty()
        }
    }
}
