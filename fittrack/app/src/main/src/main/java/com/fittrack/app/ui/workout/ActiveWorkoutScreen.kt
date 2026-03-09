package com.fittrack.app.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fittrack.app.data.remote.WgerImageRepository
import com.fittrack.app.domain.model.Exercise
import com.fittrack.app.domain.model.ExerciseSet
import com.fittrack.app.ui.components.ExerciseImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onWorkoutComplete: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    val previousSets by viewModel.previousSets.collectAsStateWithLifecycle()
    val estimatedMaxes by viewModel.estimatedOneRepMaxes.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()
    val imageRepository = viewModel.imageRepository
    val showExercisePicker by viewModel.showExercisePicker.collectAsStateWithLifecycle()
    val availableExercises by viewModel.availableExercises.collectAsStateWithLifecycle()
    val restTimer by viewModel.restTimer.collectAsStateWithLifecycle()
    val restDuration by viewModel.restDurationSeconds.collectAsStateWithLifecycle()
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
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

                RestDurationSelector(
                    currentSeconds = restDuration,
                    onSelect = { viewModel.setRestDuration(it) }
                )

                AnimatedVisibility(
                    visible = restTimer.isActive,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    RestTimerBanner(
                        state = restTimer,
                        onDismiss = { viewModel.dismissRestTimer() },
                        onAddTime = { viewModel.setRestDuration(restDuration + 30) }
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(exercises, key = { it.sessionExercise.id }) { exerciseWithSets ->
                        val exerciseId = exerciseWithSets.sessionExercise.exerciseId
                        val prevSets = previousSets[exerciseId] ?: emptyList()
                        val estimatedMax = estimatedMaxes[exerciseId]
                        ExerciseSection(
                            exerciseWithSets = exerciseWithSets,
                            previousSets = prevSets,
                            estimatedOneRepMax = estimatedMax,
                            imageRepository = imageRepository,
                            onAddSet = { viewModel.addSet(it) },
                            onUpdateSet = { viewModel.updateSet(it) },
                            onRemoveExercise = { viewModel.removeExercise(it) }
                        )
                    }
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
                            imageRepository = imageRepository,
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
private fun RestTimerBanner(
    state: RestTimerState,
    onDismiss: () -> Unit,
    onAddTime: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = if (state.totalSeconds > 0) {
            state.remainingSeconds.toFloat() / state.totalSeconds
        } else 0f,
        label = "rest_progress"
    )
    val isFinished = state.remainingSeconds <= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFinished) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFinished) MaterialTheme.colorScheme.tertiary
                                else MaterialTheme.colorScheme.secondary
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatTimerDisplay(state.remainingSeconds),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isFinished) MaterialTheme.colorScheme.onTertiary
                            else MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isFinished) "Rest Complete!" else "Resting...",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isFinished) MaterialTheme.colorScheme.onTertiaryContainer
                        else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Row {
                    if (!isFinished) {
                        TextButton(onClick = onAddTime) {
                            Text("+30s")
                        }
                    }
                    TextButton(onClick = onDismiss) {
                        Text(if (isFinished) "Dismiss" else "Skip")
                    }
                }
            }
            if (!isFinished) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RestDurationSelector(
    currentSeconds: Int,
    onSelect: (Int) -> Unit
) {
    val presets = listOf(30, 60, 90, 120, 180, 300)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Rest:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        presets.forEach { seconds ->
            FilterChip(
                selected = currentSeconds == seconds,
                onClick = { onSelect(seconds) },
                label = {
                    Text(
                        text = formatTimerLabel(seconds),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.height(28.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun ExerciseSection(
    exerciseWithSets: WorkoutExerciseWithSets,
    previousSets: List<ExerciseSet>,
    estimatedOneRepMax: Double?,
    imageRepository: WgerImageRepository,
    onAddSet: (Long) -> Unit,
    onUpdateSet: (ExerciseSet) -> Unit,
    onRemoveExercise: (Long) -> Unit
) {
    val sessionExercise = exerciseWithSets.sessionExercise
    val exerciseName = sessionExercise.exercise?.name ?: "Exercise"
    val sets = exerciseWithSets.sets.sortedBy { it.setNumber }
    val hasPrevious = previousSets.isNotEmpty()

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseImage(
                    exerciseName = exerciseName,
                    imageRepository = imageRepository,
                    size = 44.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (estimatedOneRepMax != null && estimatedOneRepMax > 0) {
                        Text(
                            text = "Est. 1RM: ${formatWeight(estimatedOneRepMax)} lbs",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = { onRemoveExercise(sessionExercise.id) }) {
                    Icon(Icons.Default.Close, contentDescription = "Remove exercise")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Set",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(32.dp)
                )
                if (hasPrevious) {
                    Text(
                        text = "Previous",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(72.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "lbs",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
                Text(
                    text = "Reps",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(64.dp)
                )
                Text(
                    text = "\u2713",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(48.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            sets.forEach { set ->
                val prevSet = previousSets.find { it.setNumber == set.setNumber }
                SetRow(
                    set = set,
                    previousSet = prevSet,
                    hasPreviousColumn = hasPrevious,
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
    previousSet: ExerciseSet?,
    hasPreviousColumn: Boolean,
    onUpdate: (ExerciseSet) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${set.setNumber}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(32.dp)
        )
        if (hasPreviousColumn) {
            Text(
                text = previousSet?.let { formatPreviousSet(it) } ?: "\u2014",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.width(72.dp),
                textAlign = TextAlign.Center
            )
        }
        OutlinedTextField(
            value = if (set.weight > 0) formatWeight(set.weight) else "",
            onValueChange = {
                val weight = it.toDoubleOrNull() ?: 0.0
                onUpdate(set.copy(weight = weight))
            },
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .padding(horizontal = 4.dp),
            placeholder = {
                if (previousSet != null && previousSet.weight > 0) {
                    Text(
                        text = formatWeight(previousSet.weight),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        OutlinedTextField(
            value = if (set.reps > 0) set.reps.toString() else "",
            onValueChange = {
                val reps = it.toIntOrNull() ?: 0
                onUpdate(set.copy(reps = reps))
            },
            modifier = Modifier
                .width(64.dp)
                .height(56.dp),
            placeholder = {
                if (previousSet != null && previousSet.reps > 0) {
                    Text(
                        text = "${previousSet.reps}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            },
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
    imageRepository: WgerImageRepository,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExerciseImage(
                exerciseName = exercise.name,
                imageRepository = imageRepository,
                size = 40.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun formatPreviousSet(set: ExerciseSet): String {
    val w = formatWeight(set.weight)
    return "${w}\u00D7${set.reps}"
}

private fun formatWeight(weight: Double): String {
    return if (weight == weight.toLong().toDouble()) {
        weight.toLong().toString()
    } else {
        weight.toString()
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

private fun formatTimerDisplay(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}

private fun formatTimerLabel(seconds: Int): String {
    return if (seconds >= 60 && seconds % 60 == 0) {
        "${seconds / 60}m"
    } else {
        "${seconds}s"
    }
}
