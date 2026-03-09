package com.fittrack.app.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.domain.model.Equipment
import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.MuscleGroup
import com.fittrack.app.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow<MuscleGroup?>(null)
    val selectedMuscleGroup: StateFlow<MuscleGroup?> = _selectedMuscleGroup.asStateFlow()

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoading.value = true
            combine(_searchQuery, _selectedMuscleGroup) { query, muscleGroup ->
                Pair(query, muscleGroup)
            }.flatMapLatest { (query, muscleGroup) ->
                when {
                    muscleGroup != null -> exerciseRepository.getExercisesByMuscleGroup(muscleGroup)
                    query.isNotBlank() -> exerciseRepository.searchExercises(query)
                    else -> exerciseRepository.getAllExercises()
                }
            }.collect { list ->
                _exercises.value = list
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onMuscleGroupSelected(group: MuscleGroup?) {
        _selectedMuscleGroup.value = group
    }

    fun addCustomExercise(name: String, muscleGroup: MuscleGroup, equipment: Equipment) {
        viewModelScope.launch {
            exerciseRepository.insertExercise(
                Exercise(
                    name = name,
                    muscleGroup = muscleGroup,
                    equipment = equipment,
                    isCustom = true
                )
            )
        }
    }
}
