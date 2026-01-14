package com.fhzapps.bodytrack.exercises

import android.util.Log
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme
import com.fhzapps.bodytrack.ui.theme.black1
import com.fhzapps.bodytrack.ui.theme.darkGray
import com.fhzapps.bodytrack.ui.theme.lightGray
import com.fhzapps.bodytrack.ui.theme.red1
import com.fhzapps.bodytrack.ui.theme.white1
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue


@Composable
fun ExercisePageRoot(
    exerciseId: String,
    viewModel: ExerciseViewModel = koinViewModel()
) {
    LaunchedEffect(exerciseId) {
        Log.d("ExercisePageRoot", "Fetching exercise: $exerciseId")
        viewModel.getSelectedExercise(exerciseId)
    }

    ExerciseView(viewModel = viewModel)
}

@Composable
fun ExerciseView(
    viewModel: ExerciseViewModel
) {
    val exercise by viewModel.exercise.collectAsStateWithLifecycle()
    val numSets by viewModel.sets.collectAsStateWithLifecycle()
    val isLoading = exercise.exerciseId.isEmpty()

    Log.d("ExerciseView", "ExerciseView Exercise: ${exercise.name}")

    BodyTrackTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(black1)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = red1
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    ExerciseDescription(exercise)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Log Your Sets",
                        style = MaterialTheme.typography.titleLarge,
                        color = white1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    for (i in 1..numSets) {
                        val weightState = rememberTextFieldState()
                        val repsState = rememberTextFieldState()
                        SetEntry(i, weightState, repsState)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseDescription(
    movement: Movement
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkGray
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = movement.name,
                style = MaterialTheme.typography.headlineMedium,
                color = white1,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(12.dp)),
                model = movement.gifUrl,
                alignment = Alignment.Center,
                contentDescription = "Exercise Image"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (movement.instructions.isNotEmpty()) {
                Text(
                    text = movement.instructions,
                    style = MaterialTheme.typography.bodyMedium,
                    color = lightGray
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (movement.targetMuscles.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Target: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = lightGray
                    )
                    Text(
                        text = movement.targetMuscles.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = red1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (movement.equipment.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Equipment: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = lightGray
                    )
                    Text(
                        text = movement.equipment.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = white1
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${movement.targetSets} sets × ${movement.targetReps} reps",
                    style = MaterialTheme.typography.bodyLarge,
                    color = white1,
                    fontWeight = FontWeight.Medium
                )
                if (movement.lastMaxWeight > 0) {
                    Text(
                        text = "PR: ${movement.lastMaxWeight} × ${movement.lastMaxReps}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = red1
                    )
                }
            }
        }
    }
}

@Composable
fun SetEntry(
    setNumber: Int,
    weightValue: TextFieldState,
    repsValue: TextFieldState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$setNumber",
                style = MaterialTheme.typography.headlineMedium,
                color = red1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp)
            )

            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                state = weightValue,
                lineLimits = TextFieldLineLimits.SingleLine,
                placeholder = { Text("Weight", color = lightGray) },
            )

            TextField(
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                state = repsValue,
                lineLimits = TextFieldLineLimits.SingleLine,
                placeholder = { Text("Reps", color = lightGray) },
            )
        }
    }
}


@Preview
@Composable
fun ExerciseDescriptionPreview() {
    BodyTrackTheme {
        ExerciseDescription(
            Movement(
                name = "Barbell Bench Press",
                exerciseId = "0001",
                instructions = "Aim for a full range of motion with a slow negative",
                gifUrl = "https://static.exercisedb.dev/media/wnEscH8.gif",
                lastMaxWeight = 315,
                targetReps = 12,
                targetSets = 3,
                lastMaxReps = 3,
                targetMuscles = listOf("Chest", "Triceps"),
                equipment = listOf("Barbell", "Bench")
            ),
        )
    }
}

@Composable
@Preview
fun SetEntryPreview() {
    BodyTrackTheme {
        val weightState = rememberTextFieldState()
        val repsState = rememberTextFieldState()
        SetEntry(1, weightState, repsState)
    }
}