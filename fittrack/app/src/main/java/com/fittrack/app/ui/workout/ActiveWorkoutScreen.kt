package com.fittrack.app.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.ExerciseSet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onWorkoutComplete: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()
    val showExercisePicker by viewModel.showExercisePicker.collectAsStateWithLifecycle()
    val availableExercises by viewModel.availableExercises.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleExercisePicker() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add exercise")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top bar: Session name, timer, Finish button
            Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = session?.name ?: "Workout",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatElapsedTime(elapsedSeconds),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.completeWorkout()
                                onWorkoutComplete()
                            }
                        }
                    ) {
                        Text("Finish Workout")
                    }
                }
            }
        }

        // Exercise list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(exercises, key = { it.sessionExercise.id }) { exerciseWithSets ->
                ExerciseSection(
                    exerciseWithSets = exerciseWithSets,
                    onAddSet = { viewModel.addSet(it) },
                    onUpdateSet = { viewModel.updateSet(it) },
                    onRemoveExercise = { viewModel.removeExercise(it) }
                )
            }
        }
    }
    }

    if (showExercisePicker) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleExercisePicker() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Add Exercise",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableExercises, key = { it.id }) { exercise ->
                        ExercisePickerItem(
                            exercise = exercise,
                            onClick = {
                                viewModel.addExercise(exercise.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseSection(
    exerciseWithSets: WorkoutExerciseWithSets,
    onAddSet: (Long) -> Unit,
    onUpdateSet: (ExerciseSet) -> Unit,
    onRemoveExercise: (Long) -> Unit
) {
    val sessionExercise = exerciseWithSets.sessionExercise
    val exerciseName = sessionExercise.exercise?.name ?: "Exercise"
    val sets = exerciseWithSets.sets.sortedBy { it.setNumber }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { onRemoveExercise(sessionExercise.id) }) {
                    Icon(Icons.Default.Close, contentDescription = "Remove exercise")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Table header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Set",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(40.dp)
                )
                Text(
                    text = "Weight (lbs)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(100.dp)
                )
                Text(
                    text = "Reps",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            sets.forEach { set ->
                SetRow(
                    set = set,
                    onUpdate = onUpdateSet
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Button(
                onClick = { onAddSet(sessionExercise.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.height(16.dp))
                Text("Add Set", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun SetRow(
    set: ExerciseSet,
    onUpdate: (ExerciseSet) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${set.setNumber}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(40.dp)
        )
        OutlinedTextField(
            value = if (set.weight > 0) set.weight.toString() else "",
            onValueChange = {
                val weight = it.toDoubleOrNull() ?: 0.0
                onUpdate(set.copy(weight = weight))
            },
            modifier = Modifier.width(100.dp).height(56.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        OutlinedTextField(
            value = if (set.reps > 0) set.reps.toString() else "",
            onValueChange = {
                val reps = it.toIntOrNull() ?: 0
                onUpdate(set.copy(reps = reps))
            },
            modifier = Modifier.width(80.dp).height(56.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Checkbox(
            checked = set.isComplete,
            onCheckedChange = { onUpdate(set.copy(isComplete = it)) }
        )
    }
}

@Composable
private fun ExercisePickerItem(
    exercise: Exercise,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun formatElapsedTime(elapsedSeconds: Int): String {
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    return when {
        hours > 0 -> "%d:%02d:%02d".format(hours, minutes, seconds)
        else -> "%02d:%02d".format(minutes, seconds)
    }
}
