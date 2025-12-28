package com.fhzapps.bodytrack.exercises

import android.util.Log
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme
import com.fhzapps.bodytrack.ui.theme.red1
import androidx.compose.runtime.collectAsState


@Composable
fun ExercisePageRoot(){
    ExerciseView()
}

@Composable
fun ExerciseView(
    viewModel: ExerciseViewModel = koinViewModel()
){
    val exercise = viewModel.exercise.collectAsState().value
    val weightState = rememberTextFieldState()
    val repsState = rememberTextFieldState()
    val numSets = remember { viewModel.sets }

    Log.d("ExerciseView", "ExerciseView Exercise: $exercise")
    BodyTrackTheme {
        Column {
            ExerciseDescription( exercise )
            for (i in 1..numSets.collectAsState().value){
                SetEntry(i,weightState,repsState)
                //need to remove hoisting of weight and reps state
                //pass in exercise to get exerciseID and timestamp and use that to index the set
            }
        }
    }

}

@Composable
fun ExerciseDescription(
    movement: Movement
) {
    BodyTrackTheme {
        Box (
            modifier = Modifier.fillMaxWidth(),
        ){
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(4.dp),
            ){
                Text(
                    text = movement.name,
                    style = MaterialTheme.typography.bodyLarge,
                )
                AsyncImage(
                    modifier = Modifier.size(250.dp),
                    model = movement.gifUrl,
                    alignment = Alignment.Center,
                    contentDescription = "Exercise Image")
                Text(
                    text = movement.instructions,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.size(18.dp))
                Row {
                    Text(
                        text = "${movement.targetSets} sets of ",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "${movement.targetReps} reps",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Last max: ${movement.lastMaxWeight} for ${movement.lastMaxReps} reps",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun SetEntry(
    setNumber: Int,
    weightValue : TextFieldState,
    repsValue : TextFieldState,
    modifier: Modifier = Modifier
){
    BodyTrackTheme {
        Row (
            modifier = modifier.fillMaxWidth().padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = "$setNumber",
                color = red1,
                fontSize = 40.sp,

                )

            //weight of each set
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                state = weightValue,
                lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 2),
                placeholder = { Text("Weight") },
            )

            // number of reps
            TextField(
                modifier = Modifier.padding(20.dp)
                    .weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                state = repsValue,
                lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 2),
                placeholder = { Text("Reps") },
            )

        }
    }
}


@Preview
@Composable
fun ExerciseDescriptionPreview(){
    ExerciseDescription(
        Movement(
            name = "Barbell Bench Press",
            exerciseId = "0001",
            instructions = "Aim for a full range of motion \nwith a slow negative",
            gifUrl = "https://static.exercisedb.dev/media/wnEscH8.gif",
            lastMaxWeight = 315, targetReps = 12, targetSets = 3, lastMaxReps = 3
        ),
    )
}

@Composable
@Preview
fun SetEntryPreview(){
    val weightState = rememberTextFieldState()
    val repsState = rememberTextFieldState()
    SetEntry(1,weightState,repsState)
}