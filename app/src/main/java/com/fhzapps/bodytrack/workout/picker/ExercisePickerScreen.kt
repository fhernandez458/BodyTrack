package com.fhzapps.bodytrack.workout.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fhzapps.bodytrack.data.MovementEntity
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme
import com.fhzapps.bodytrack.workout.create.AddExerciseDialog
import com.fhzapps.bodytrack.ui.theme.black1
import com.fhzapps.bodytrack.ui.theme.darkGray
import com.fhzapps.bodytrack.ui.theme.green1
import com.fhzapps.bodytrack.ui.theme.lightGray
import com.fhzapps.bodytrack.ui.theme.white1
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExercisePickerRoot(
    onExercisesSelected: (List<String>) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ExercisePickerViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isConfirmed) {
        if (uiState.isConfirmed) {
            onExercisesSelected(viewModel.getSelectedExerciseIds())
        }
    }

    BodyTrackTheme {
        ExercisePickerScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerScreen(
    uiState: ExercisePickerUiState,
    onEvent: (ExercisePickerEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        containerColor = black1,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Exercises",
                        color = white1,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = white1,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(ExercisePickerEvent.OnShowAddExerciseDialog) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add custom exercise",
                            tint = green1,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = black1,
                ),
            )
        },
        bottomBar = {
            if (uiState.selectedExerciseIds.isNotEmpty()) {
                Button(
                    onClick = { onEvent(ExercisePickerEvent.OnConfirmSelection) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = green1,
                        contentColor = white1,
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add ${uiState.selectedExerciseIds.size} Exercise${if (uiState.selectedExerciseIds.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(black1),
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { onEvent(ExercisePickerEvent.OnSearchQueryChanged(it)) },
                placeholder = { Text("Search exercises...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = lightGray,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = white1,
                    unfocusedTextColor = white1,
                    focusedBorderColor = green1,
                    unfocusedBorderColor = darkGray,
                    focusedPlaceholderColor = lightGray,
                    unfocusedPlaceholderColor = lightGray,
                    cursorColor = white1,
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
            )

            // Body part chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.bodyParts) { bodyPart ->
                    FilterChip(
                        selected = bodyPart == uiState.selectedBodyPart,
                        onClick = { onEvent(ExercisePickerEvent.OnBodyPartSelected(bodyPart)) },
                        label = {
                            Text(
                                text = bodyPart.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (bodyPart == uiState.selectedBodyPart) black1 else white1,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = darkGray,
                            selectedContainerColor = green1,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Exercise list
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.selectedBodyPart.isEmpty() -> {
                        Text(
                            text = "Select a body part to see exercises",
                            color = lightGray,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = white1,
                        )
                    }

                    else -> {
                        val filteredExercises = if (uiState.searchQuery.isBlank()) {
                            uiState.exercises
                        } else {
                            uiState.exercises.filter {
                                it.name.contains(uiState.searchQuery, ignoreCase = true)
                            }
                        }

                        if (filteredExercises.isEmpty()) {
                            Text(
                                text = "No exercises found",
                                color = lightGray,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(
                                    items = filteredExercises,
                                    key = { it.exerciseId },
                                ) { exercise ->
                                    ExercisePickerItem(
                                        exercise = exercise,
                                        isSelected = exercise.exerciseId in uiState.selectedExerciseIds,
                                        onToggle = {
                                            onEvent(ExercisePickerEvent.OnExerciseToggled(exercise.exerciseId))
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Exercise Dialog
    if (uiState.showAddExerciseDialog) {
        AddExerciseDialog(
            initialBodyPart = uiState.selectedBodyPart.takeIf { it.isNotEmpty() },
            onDismiss = { onEvent(ExercisePickerEvent.OnDismissAddExerciseDialog) },
            onConfirm = { data ->
                onEvent(
                    ExercisePickerEvent.OnCreateExercise(
                        name = data.name,
                        bodyPart = data.bodyPart,
                        instructions = data.instructions,
                        equipment = data.equipment,
                        targetMuscles = data.targetMuscles,
                    )
                )
            },
            nameError = uiState.addExerciseError,
        )
    }
}

@Composable
private fun ExercisePickerItem(
    exercise: MovementEntity,
    isSelected: Boolean,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) darkGray.copy(alpha = 0.8f) else darkGray,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = green1,
                    uncheckedColor = lightGray,
                    checkmarkColor = white1,
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = white1,
                )
                if (exercise.targetMuscles.isNotEmpty()) {
                    Text(
                        text = exercise.targetMuscles.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = lightGray,
                    )
                }
            }
        }
    }
}
