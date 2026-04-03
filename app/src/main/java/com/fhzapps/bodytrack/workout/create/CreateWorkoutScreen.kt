package com.fhzapps.bodytrack.workout.create

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.fhzapps.bodytrack.ui.theme.black1
import com.fhzapps.bodytrack.ui.theme.darkGray
import com.fhzapps.bodytrack.ui.theme.green1
import com.fhzapps.bodytrack.ui.theme.lightGray
import com.fhzapps.bodytrack.ui.theme.red1
import com.fhzapps.bodytrack.ui.theme.white1
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateWorkoutRoot(
    workoutId: Long? = null,
    onNavigateBack: () -> Unit,
    onNavigateToExercisePicker: () -> Unit,
    selectedExerciseIds: List<String>? = null,
    viewModel: CreateWorkoutViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workoutId) {
        workoutId?.let {
            viewModel.onEvent(CreateWorkoutEvent.OnLoadWorkout(it))
        }
    }

    LaunchedEffect(selectedExerciseIds) {
        selectedExerciseIds?.let { ids ->
            if (ids.isNotEmpty()) {
                viewModel.onEvent(CreateWorkoutEvent.OnExercisesSelected(ids))
            }
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    BodyTrackTheme {
        CreateWorkoutScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onAddExercises = onNavigateToExercisePicker,
            isEditMode = workoutId != null,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    uiState: CreateWorkoutUiState,
    onEvent: (CreateWorkoutEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onAddExercises: () -> Unit,
    isEditMode: Boolean,
) {
    Scaffold(
        containerColor = black1,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Workout" else "Create Workout",
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = { onEvent(CreateWorkoutEvent.OnNameChanged(it)) },
                        label = { Text("Workout Name") },
                        isError = uiState.nameError != null,
                        supportingText = uiState.nameError?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = white1,
                            unfocusedTextColor = white1,
                            focusedBorderColor = green1,
                            unfocusedBorderColor = lightGray,
                            focusedLabelColor = green1,
                            unfocusedLabelColor = lightGray,
                            cursorColor = white1,
                            errorBorderColor = red1,
                            errorLabelColor = red1,
                        ),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = { onEvent(CreateWorkoutEvent.OnDescriptionChanged(it)) },
                        label = { Text("Description (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = white1,
                            unfocusedTextColor = white1,
                            focusedBorderColor = green1,
                            unfocusedBorderColor = lightGray,
                            focusedLabelColor = green1,
                            unfocusedLabelColor = lightGray,
                            cursorColor = white1,
                        ),
                        maxLines = 3,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Exercises (${uiState.selectedExercises.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = white1,
                            fontWeight = FontWeight.Bold,
                        )
                        Button(
                            onClick = onAddExercises,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = darkGray,
                                contentColor = white1,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                    ) {
                        items(
                            items = uiState.selectedExercises,
                            key = { it.exerciseId },
                        ) { exercise ->
                            ExerciseItem(
                                exercise = exercise,
                                onRemove = {
                                    onEvent(CreateWorkoutEvent.OnExerciseRemoved(exercise.exerciseId))
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onEvent(CreateWorkoutEvent.OnSave) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = green1,
                            contentColor = white1,
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !uiState.isLoading,
                    ) {
                        Text(
                            text = if (isEditMode) "Save Changes" else "Create Workout",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseItem(
    exercise: MovementEntity,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = darkGray,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "Reorder",
                tint = lightGray,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = white1,
                )
                Text(
                    text = exercise.bodyPart.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = lightGray,
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = lightGray,
                )
            }
        }
    }
}
