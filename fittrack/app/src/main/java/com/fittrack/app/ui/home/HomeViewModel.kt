package com.fittrack.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.domain.model.WorkoutSession
import com.fittrack.app.domain.repository.WorkoutRepository
import com.fittrack.app.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            combine(
                workoutRepository.getRecentSessions(limit = 5),
                workoutRepository.getAllSessions()
            ) { recent, all ->
                HomeState(
                    recentSessions = recent,
                    totalWorkouts = all.size,
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    suspend fun startNewWorkout(): Long {
        val dateFormat = SimpleDateFormat("MMM d", Locale.US)
        val dateStr = dateFormat.format(Date())
        val name = "Workout - $dateStr"
        return workoutRepository.startSession(name)
    }
}

data class HomeState(
    val recentSessions: List<WorkoutSession> = emptyList(),
    val totalWorkouts: Int = 0,
    val isLoading: Boolean = false
)
