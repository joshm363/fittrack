package com.fittrack.app.ui.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.data.local.UserPreferencesManager
import com.fittrack.app.data.remote.WgerImageRepository
import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.ExerciseSet
import com.fittrack.app.domain.model.SessionExercise
import com.fittrack.app.domain.model.WorkoutSession
import com.fittrack.app.domain.repository.ExerciseRepository
import com.fittrack.app.domain.repository.WorkoutRepository
import com.fittrack.app.domain.suggestion.OneRepMaxCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutExerciseWithSets(
    val sessionExercise: SessionExercise,
    val sets: List<ExerciseSet>
)

data class RestTimerState(
    val isActive: Boolean = false,
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 90
)

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val preferencesManager: UserPreferencesManager,
    val imageRepository: WgerImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<Long>("sessionId") ?: 0L

    private val _session = MutableStateFlow<WorkoutSession?>(null)
    val session: StateFlow<WorkoutSession?> = _session.asStateFlow()

    private val _exercises = MutableStateFlow<List<WorkoutExerciseWithSets>>(emptyList())
    val exercises: StateFlow<List<WorkoutExerciseWithSets>> = _exercises.asStateFlow()

    private val _previousSets = MutableStateFlow<Map<Long, List<ExerciseSet>>>(emptyMap())
    val previousSets: StateFlow<Map<Long, List<ExerciseSet>>> = _previousSets.asStateFlow()

    private val _estimatedOneRepMaxes = MutableStateFlow<Map<Long, Double>>(emptyMap())
    val estimatedOneRepMaxes: StateFlow<Map<Long, Double>> = _estimatedOneRepMaxes.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(true)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _showExercisePicker = MutableStateFlow(false)
    val showExercisePicker: StateFlow<Boolean> = _showExercisePicker.asStateFlow()

    private val _availableExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val availableExercises: StateFlow<List<Exercise>> = _availableExercises.asStateFlow()

    private val _restTimer = MutableStateFlow(RestTimerState(totalSeconds = preferencesManager.getRestTimerSeconds()))
    val restTimer: StateFlow<RestTimerState> = _restTimer.asStateFlow()

    private val _restDurationSeconds = MutableStateFlow(preferencesManager.getRestTimerSeconds())
    val restDurationSeconds: StateFlow<Int> = _restDurationSeconds.asStateFlow()

    private var restTimerJob: Job? = null

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
                loadHistoricalData(list)
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

    private suspend fun loadHistoricalData(exercises: List<WorkoutExerciseWithSets>) {
        val currentPrev = _previousSets.value
        val current1rm = _estimatedOneRepMaxes.value
        val exerciseIds = exercises.mapNotNull { it.sessionExercise.exerciseId.takeIf { id -> id > 0 } }
        val newIds = exerciseIds.filter { it !in currentPrev }
        if (newIds.isEmpty()) return

        val updatedPrev = currentPrev.toMutableMap()
        val updated1rm = current1rm.toMutableMap()
        newIds.forEach { exerciseId ->
            updatedPrev[exerciseId] = workoutRepository.getPreviousSetsForExercise(exerciseId)
            val allSets = workoutRepository.getAllCompletedSetsForExercise(exerciseId)
            val orm = OneRepMaxCalculator.estimateFromHistory(allSets)
            if (orm > 0) updated1rm[exerciseId] = orm
        }
        _previousSets.value = updatedPrev
        _estimatedOneRepMaxes.value = updated1rm
    }

    fun toggleExercisePicker() {
        viewModelScope.launch {
            val newValue = !_showExercisePicker.value
            _showExercisePicker.value = newValue
            if (newValue) {
                val equipment = preferencesManager.getAvailableEquipment()
                exerciseRepository.getAllExercises().first().let { list ->
                    _availableExercises.value = list.filter { it.equipment in equipment }
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
            val exerciseId = exerciseWithSets?.sessionExercise?.exerciseId ?: 0L

            val prevSets = _previousSets.value[exerciseId] ?: emptyList()
            val matchingPrev = prevSets.find { it.setNumber == nextSetNumber }
            val orm = _estimatedOneRepMaxes.value[exerciseId]

            val targetReps = matchingPrev?.reps ?: 0
            val suggestedWeight = if (orm != null && orm > 0 && targetReps > 0) {
                OneRepMaxCalculator.weightForReps(orm, targetReps)
            } else {
                matchingPrev?.weight ?: 0.0
            }

            workoutRepository.addSet(
                sessionExerciseId = sessionExerciseId,
                setNumber = nextSetNumber,
                weight = suggestedWeight,
                reps = targetReps
            )
        }
    }

    fun updateSet(set: ExerciseSet) {
        viewModelScope.launch {
            workoutRepository.updateSet(set)
        }
        if (set.isComplete) {
            startRestTimer()
        }
    }

    fun setRestDuration(seconds: Int) {
        _restDurationSeconds.value = seconds
        preferencesManager.setRestTimerSeconds(seconds)
        if (_restTimer.value.isActive) {
            startRestTimer()
        }
    }

    fun startRestTimer() {
        restTimerJob?.cancel()
        val duration = _restDurationSeconds.value
        _restTimer.value = RestTimerState(
            isActive = true,
            remainingSeconds = duration,
            totalSeconds = duration
        )
        restTimerJob = viewModelScope.launch {
            var remaining = duration
            while (remaining > 0) {
                delay(1000)
                remaining--
                _restTimer.update { it.copy(remainingSeconds = remaining) }
            }
            delay(3000)
            _restTimer.update { it.copy(isActive = false) }
        }
    }

    fun dismissRestTimer() {
        restTimerJob?.cancel()
        _restTimer.update { it.copy(isActive = false, remainingSeconds = 0) }
    }

    fun completeWorkout() {
        viewModelScope.launch {
            workoutRepository.completeSession(sessionId)
            _isRunning.value = false
        }
    }
}
