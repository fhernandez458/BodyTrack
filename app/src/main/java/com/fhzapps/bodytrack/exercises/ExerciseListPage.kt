package com.fhzapps.bodytrack.exercises

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.fhzapps.bodytrack.BodyPage.BodyPageViewmodel
import com.fhzapps.bodytrack.BodyParts.BodyPart
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme
import com.fhzapps.bodytrack.ui.theme.black1
import com.fhzapps.bodytrack.ui.theme.darkGray
import com.fhzapps.bodytrack.ui.theme.lightGray
import com.fhzapps.bodytrack.ui.theme.white1
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun ExerciseListRoot(
    exerciseViewModel: ExerciseViewModel = koinViewModel(),
    bodyPageViewmodel: BodyPageViewmodel = koinViewModel(),
    onExerciseClicked: () -> Unit
) {
    val bodyPart = bodyPageViewmodel.currentBodyPart
    val exercises = exerciseViewModel.getAllExercisesForBodyPart(bodyPart = bodyPart)

    BodyTrackTheme {
        ExerciseListPage(
            bodyPart = bodyPart,
            exercises = exercises,
            onExerciseClicked = {
                onExerciseClicked()
                Log.d("ExerciseListRoot", "Clicked Exercise with ID $it")
                exerciseViewModel.getSelectedExercise("HEJ6DIX")
            }
        )
    }
}

@Composable
fun ExerciseListPage(
    bodyPart: BodyPart,
    exercises: List<Exercise>,
    onExerciseClicked: (exerciseId: String) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize()
            .background(black1),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(exercises.size) { index ->
            ExerciseListItem(
                exercise = exercises[index],
                onClick = {
                    onExerciseClicked(it.exerciseId)
                }
            )
        }
    }
}


@Composable
fun ExerciseListItem (
    exercise: Exercise,
    onClick : (Exercise) -> Unit,
) {
    BodyTrackTheme {
        Button (
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                vertical = 8.dp
            ),
            shape = RoundedCornerShape(40f),
            colors = ButtonColors(
                containerColor = darkGray,
                contentColor = white1,
                disabledContainerColor = lightGray,
                disabledContentColor = lightGray
            ),
            onClick = {
                onClick(exercise)
                Log.d("exerciseListItem", "Clicked ${exercise.name}")
            }
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ){
                AsyncImage(
                    model = exercise.gifUrl,
                    contentDescription = exercise.name,
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = exercise.name,
                    modifier = Modifier.padding(start = 24.dp),
                )
            }
        }
    }


}

@Composable
@Preview
fun ExerciseListItemPreview() {
    ExerciseListItem(Exercise.DEFAULT, {})
}