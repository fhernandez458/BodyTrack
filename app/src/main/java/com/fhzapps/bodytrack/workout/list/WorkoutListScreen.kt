package com.fhzapps.bodytrack.workout.list

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fhzapps.bodytrack.data.workout.WorkoutEntity
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme
import com.fhzapps.bodytrack.ui.theme.black1
import com.fhzapps.bodytrack.ui.theme.darkGray
import com.fhzapps.bodytrack.ui.theme.green1
import com.fhzapps.bodytrack.ui.theme.lightGray
import com.fhzapps.bodytrack.ui.theme.white1
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutListRoot(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToSession: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToExercises: () -> Unit,
    viewModel: WorkoutListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.startedSessionId) {
        uiState.startedSessionId?.let { sessionId ->
            viewModel.onEvent(WorkoutListEvent.ClearStartedSession)
            onNavigateToSession(sessionId)
        }
    }

    BodyTrackTheme {
        WorkoutListScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToEdit = onNavigateToEdit,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToExercises = onNavigateToExercises,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    uiState: WorkoutListUiState,
    onEvent: (WorkoutListEvent) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToExercises: () -> Unit,
) {
    Scaffold(
        containerColor = black1,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Workouts",
                        color = white1,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = black1,
                ),
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = white1,
                        )
                    }
                    IconButton(onClick = onNavigateToExercises) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "Browse Exercises",
                            tint = white1,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = green1,
                contentColor = white1,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Workout",
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(black1),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = white1,
                    )
                }

                uiState.workouts.isEmpty() -> {
                    EmptyWorkoutList(
                        onCreateClick = onNavigateToCreate,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = uiState.workouts,
                            key = { it.id },
                        ) { workout ->
                            WorkoutCard(
                                workout = workout,
                                onStartClick = { onEvent(WorkoutListEvent.OnStartWorkout(workout.id)) },
                                onEditClick = { onNavigateToEdit(workout.id) },
                                onDeleteClick = { onEvent(WorkoutListEvent.OnDeleteWorkout(workout.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyWorkoutList(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = lightGray,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Workouts Yet",
            style = MaterialTheme.typography.headlineSmall,
            color = white1,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first workout to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = lightGray,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = green1,
                contentColor = white1,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Workout")
        }
    }
}

@Composable
private fun WorkoutCard(
    workout: WorkoutEntity,
    onStartClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = darkGray,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workout.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = white1,
                        fontWeight = FontWeight.Bold,
                    )
                    if (workout.description.isNotBlank()) {
                        Text(
                            text = workout.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = lightGray,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    Text(
                        text = "Created ${dateFormat.format(Date(workout.createdAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = lightGray,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = lightGray,
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = lightGray,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStartClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green1,
                    contentColor = white1,
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Workout")
            }
        }
    }
}
