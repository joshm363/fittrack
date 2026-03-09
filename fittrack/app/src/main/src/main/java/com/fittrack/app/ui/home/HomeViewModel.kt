package com.fittrack.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.data.local.UserPreferencesManager
import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.WorkoutSession
import com.fittrack.app.domain.model.WorkoutStrategy
import com.fittrack.app.domain.repository.WorkoutRepository
import com.fittrack.app.domain.suggestion.WorkoutSuggestion
import com.fittrack.app.domain.suggestion.WorkoutSuggestionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val suggestionEngine: WorkoutSuggestionEngine,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        _state.update { it.copy(strategy = preferencesManager.getWorkoutStrategy()) }
        loadRecentSessions()
        loadSuggestion()
    }

    private fun loadRecentSessions() {
        viewModelScope.launch {
            workoutRepository.getRecentSessions(limit = 5).collect { recent ->
                _state.update { it.copy(recentSessions = recent, isLoading = false) }
            }
        }
    }

    private fun loadSuggestion() {
        viewModelScope.launch {
            val strategy = _state.value.strategy
            val suggestion = suggestionEngine.suggest(strategy)
            _state.update { it.copy(suggestion = suggestion) }
        }
    }

    fun setStrategy(strategy: WorkoutStrategy) {
        preferencesManager.setWorkoutStrategy(strategy)
        _state.update { it.copy(strategy = strategy, suggestion = null) }
        loadSuggestion()
    }

    suspend fun startNewWorkout(): Long {
        val dateFormat = SimpleDateFormat("MMM d", Locale.US)
        val dateStr = dateFormat.format(Date())
        val name = "Workout - $dateStr"
        return workoutRepository.startSession(name)
    }

    suspend fun startSuggestedWorkout(): Long {
        val suggestion = _state.value.suggestion ?: return startNewWorkout()
        val sessionId = workoutRepository.startSession(suggestion.dayLabel)
        suggestion.exercises.forEachIndexed { index, exercise ->
            workoutRepository.addExerciseToSession(sessionId, exercise.id, index)
        }
        return sessionId
    }
}

data class HomeState(
    val recentSessions: List<WorkoutSession> = emptyList(),
    val isLoading: Boolean = true,
    val strategy: WorkoutStrategy = WorkoutStrategy.PUSH_PULL_LEGS,
    val suggestion: WorkoutSuggestion? = null
)
