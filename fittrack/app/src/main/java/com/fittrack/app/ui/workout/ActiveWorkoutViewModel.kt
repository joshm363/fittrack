package com.fittrack.app.ui.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.ExerciseSet
import com.fittrack.app.domain.model.SessionExercise
import com.fittrack.app.domain.model.WorkoutSession
import com.fittrack.app.domain.repository.ExerciseRepository
import com.fittrack.app.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutExerciseWithSets(
    val sessionExercise: SessionExercise,
    val sets: List<ExerciseSet>
)

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<String>("sessionId")?.toLongOrNull() ?: 0L

    private val _session = MutableStateFlow<WorkoutSession?>(null)
    val session: StateFlow<WorkoutSession?> = _session.asStateFlow()

    private val _exercises = MutableStateFlow<List<WorkoutExerciseWithSets>>(emptyList())
    val exercises: StateFlow<List<WorkoutExerciseWithSets>> = _exercises.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(true)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _showExercisePicker = MutableStateFlow(false)
    val showExercisePicker: StateFlow<Boolean> = _showExercisePicker.asStateFlow()

    private val _availableExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val availableExercises: StateFlow<List<Exercise>> = _availableExercises.asStateFlow()

    init {
        viewModelScope.launch {
            _session.value = workoutRepository.getSessionById(sessionId)
        }

        viewModelScope.launch {
            workoutRepository.getSessionExercises(sessionId).flatMapLatest { sessionExercises ->
                if (sessionExercises.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(
                        sessionExercises.map { se ->
                            workoutRepository.getSetsForSessionExercise(se.id).map { sets ->
                                WorkoutExerciseWithSets(se, sets)
                            }
                        }
                    ) { it.toList() }
                }
            }.collect { list ->
                _exercises.value = list
            }
        }

        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_isRunning.value) {
                    _elapsedSeconds.value = _elapsedSeconds.value + 1
                }
            }
        }
    }

    fun toggleExercisePicker() {
        viewModelScope.launch {
            val newValue = !_showExercisePicker.value
            _showExercisePicker.value = newValue
            if (newValue) {
                exerciseRepository.getAllExercises().first().let { list ->
                    _availableExercises.value = list
                }
            }
        }
    }

    fun addExercise(exerciseId: Long) {
        viewModelScope.launch {
            val currentExercises = _exercises.value
            val nextSortOrder = currentExercises.size
            workoutRepository.addExerciseToSession(sessionId, exerciseId, nextSortOrder)
            _showExercisePicker.value = false
        }
    }

    fun removeExercise(sessionExerciseId: Long) {
        viewModelScope.launch {
            workoutRepository.removeExerciseFromSession(sessionExerciseId)
        }
    }

    fun addSet(sessionExerciseId: Long) {
        viewModelScope.launch {
            val exerciseWithSets = _exercises.value.find { it.sessionExercise.id == sessionExerciseId }
            val nextSetNumber = (exerciseWithSets?.sets?.maxOfOrNull { it.setNumber } ?: 0) + 1
            workoutRepository.addSet(sessionExerciseId, nextSetNumber)
        }
    }

    fun updateSet(set: ExerciseSet) {
        viewModelScope.launch {
            workoutRepository.updateSet(set)
        }
    }

    fun completeWorkout() {
        viewModelScope.launch {
            workoutRepository.completeSession(sessionId)
            _isRunning.value = false
        }
    }
}
