package com.fhzapps.bodytrack.workout.session

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fhzapps.bodytrack.data.WeightUnit
import com.fhzapps.bodytrack.data.workout.SessionExerciseWithSets
import com.fhzapps.bodytrack.data.workout.SessionSetEntity
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme
import com.fhzapps.bodytrack.ui.theme.black1
import com.fhzapps.bodytrack.ui.theme.darkGray
import com.fhzapps.bodytrack.ui.theme.green1
import com.fhzapps.bodytrack.ui.theme.lightGray
import com.fhzapps.bodytrack.ui.theme.red1
import com.fhzapps.bodytrack.ui.theme.white1
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveSessionRoot(
    sessionId: Long,
    onSessionComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ActiveSessionViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(sessionId) {
        viewModel.onEvent(ActiveSessionEvent.OnLoadSession(sessionId))
    }

    LaunchedEffect(uiState.isCompleted, uiState.isDiscarded) {
        if (uiState.isCompleted || uiState.isDiscarded) {
            onSessionComplete()
        }
    }

    BodyTrackTheme {
        ActiveSessionScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSessionScreen(
    uiState: ActiveSessionUiState,
    onEvent: (ActiveSessionEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    var showFinishDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var sessionNotes by remember { mutableStateOf("") }

    Scaffold(
        containerColor = black1,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.session?.workoutName ?: "Workout",
                            color = white1,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = lightGray,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatTime(uiState.elapsedSeconds),
                                color = lightGray,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showDiscardDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = white1,
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showFinishDialog = true }) {
                        Text(
                            text = "Finish",
                            color = green1,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = black1,
                ),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(black1),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = white1,
                )
            } else if (uiState.exercises.isEmpty()) {
                Text(
                    text = "No exercises in this workout",
                    color = lightGray,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Exercise tabs
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        itemsIndexed(uiState.exercises) { index, exercise ->
                            FilterChip(
                                selected = index == uiState.currentExerciseIndex,
                                onClick = { onEvent(ActiveSessionEvent.OnNavigateToExercise(index)) },
                                label = {
                                    Text(
                                        text = "${index + 1}",
                                        color = if (index == uiState.currentExerciseIndex) black1 else white1,
                                    )
                                },
                                leadingIcon = if (exercise.sessionExercise.isCompleted) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            modifier = Modifier.size(16.dp),
                                            tint = if (index == uiState.currentExerciseIndex) black1 else green1,
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = darkGray,
                                    selectedContainerColor = green1,
                                ),
                            )
                        }
                    }

                    // Current exercise
                    val currentExercise = uiState.exercises.getOrNull(uiState.currentExerciseIndex)
                    currentExercise?.let { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onLogSet = { weight, reps, unit ->
                                onEvent(
                                    ActiveSessionEvent.OnLogSet(
                                        sessionExerciseId = exercise.sessionExercise.id,
                                        weight = weight,
                                        reps = reps,
                                        unit = unit,
                                    )
                                )
                            },
                            onDeleteSet = { setId ->
                                onEvent(ActiveSessionEvent.OnDeleteSet(setId))
                            },
                            onCompleteExercise = {
                                onEvent(ActiveSessionEvent.OnCompleteExercise(exercise.sessionExercise.id))
                            },
                            canGoNext = uiState.currentExerciseIndex < uiState.exercises.size - 1,
                            onNext = {
                                onEvent(ActiveSessionEvent.OnNavigateToExercise(uiState.currentExerciseIndex + 1))
                            },
                            canGoPrevious = uiState.currentExerciseIndex > 0,
                            onPrevious = {
                                onEvent(ActiveSessionEvent.OnNavigateToExercise(uiState.currentExerciseIndex - 1))
                            },
                        )
                    }
                }
            }
        }
    }

    // Finish dialog
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish Workout?", color = white1) },
            text = {
                Column {
                    Text("Add any notes about this workout:", color = lightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sessionNotes,
                        onValueChange = { sessionNotes = it },
                        placeholder = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = white1,
                            unfocusedTextColor = white1,
                            focusedBorderColor = green1,
                            unfocusedBorderColor = lightGray,
                            cursorColor = white1,
                        ),
                        maxLines = 3,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEvent(ActiveSessionEvent.OnCompleteSession(sessionNotes))
                        showFinishDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = green1),
                ) {
                    Text("Finish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Cancel", color = lightGray)
                }
            },
            containerColor = darkGray,
        )
    }

    // Discard dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard Workout?", color = white1) },
            text = { Text("All progress will be lost.", color = lightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        onEvent(ActiveSessionEvent.OnDiscardSession)
                        showDiscardDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = red1),
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancel", color = lightGray)
                }
            },
            containerColor = darkGray,
        )
    }
}

@Composable
private fun ExerciseCard(
    exercise: SessionExerciseWithSets,
    onLogSet: (Double, Int, WeightUnit) -> Unit,
    onDeleteSet: (Long) -> Unit,
    onCompleteExercise: () -> Unit,
    canGoNext: Boolean,
    onNext: () -> Unit,
    canGoPrevious: Boolean,
    onPrevious: () -> Unit,
) {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(WeightUnit.LBS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Exercise name
        Text(
            text = exercise.sessionExercise.exerciseName,
            style = MaterialTheme.typography.headlineSmall,
            color = white1,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Set input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = darkGray),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = "Log Set",
                    style = MaterialTheme.typography.titleMedium,
                    color = white1,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Weight") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = white1,
                            unfocusedTextColor = white1,
                            focusedBorderColor = green1,
                            unfocusedBorderColor = lightGray,
                            focusedLabelColor = green1,
                            unfocusedLabelColor = lightGray,
                            cursorColor = white1,
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it.filter { c -> c.isDigit() } },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = white1,
                            unfocusedTextColor = white1,
                            focusedBorderColor = green1,
                            unfocusedBorderColor = lightGray,
                            focusedLabelColor = green1,
                            unfocusedLabelColor = lightGray,
                            cursorColor = white1,
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = unit == WeightUnit.LBS,
                        onClick = { unit = WeightUnit.LBS },
                        label = { Text("LBS", color = if (unit == WeightUnit.LBS) black1 else white1) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = black1,
                            selectedContainerColor = green1,
                        ),
                    )
                    FilterChip(
                        selected = unit == WeightUnit.KG,
                        onClick = { unit = WeightUnit.KG },
                        label = { Text("KG", color = if (unit == WeightUnit.KG) black1 else white1) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = black1,
                            selectedContainerColor = green1,
                        ),
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            val w = weight.toDoubleOrNull() ?: 0.0
                            val r = reps.toIntOrNull() ?: 0
                            if (w > 0 && r > 0) {
                                onLogSet(w, r, unit)
                                weight = ""
                                reps = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = green1),
                        enabled = weight.isNotBlank() && reps.isNotBlank(),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Set")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sets list
        Text(
            text = "Sets (${exercise.sets.size})",
            style = MaterialTheme.typography.titleMedium,
            color = white1,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = exercise.sets.sortedBy { it.setNumber },
                key = { it.id },
            ) { set ->
                SetItem(
                    set = set,
                    onDelete = { onDeleteSet(set.id) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = onPrevious,
                enabled = canGoPrevious,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = if (canGoPrevious) white1 else lightGray,
                )
            }

            Button(
                onClick = onCompleteExercise,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (exercise.sessionExercise.isCompleted) darkGray else green1,
                ),
                enabled = !exercise.sessionExercise.isCompleted,
            ) {
                if (exercise.sessionExercise.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Completed")
                } else {
                    Text("Mark Complete")
                }
            }

            IconButton(
                onClick = onNext,
                enabled = canGoNext,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = if (canGoNext) white1 else lightGray,
                )
            }
        }
    }
}

@Composable
private fun SetItem(
    set: SessionSetEntity,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = darkGray),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(green1),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = set.setNumber.toString(),
                    color = white1,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${set.weight} ${set.unit.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = white1,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${set.reps} reps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = lightGray,
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = lightGray,
                )
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%d:%02d", minutes, secs)
    }
}
