package com.fhzapps.bodytrack.exercises

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.fhzapps.bodytrack.BodyPage.BodyPageEvent
import com.fhzapps.bodytrack.BodyPage.BodyPageUiState
import com.fhzapps.bodytrack.BodyPage.BodyPageViewmodel
import com.fhzapps.bodytrack.data.ExerciseListResponse
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme
import com.fhzapps.bodytrack.ui.theme.black1
import com.fhzapps.bodytrack.ui.theme.darkGray
import com.fhzapps.bodytrack.ui.theme.lightGray
import com.fhzapps.bodytrack.ui.theme.white1
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExerciseListRoot(
    bodyPart: String,
    onExerciseClicked: (exerciseId: String) -> Unit,
    bodyPageViewmodel: BodyPageViewmodel = koinViewModel()
) {
    val uiState by bodyPageViewmodel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(bodyPart) {
        Log.d("ExerciseListRoot", "Fetching exercises for body part: $bodyPart")
        bodyPageViewmodel.onEvent(BodyPageEvent.OnBodyPartSelected(bodyPart))
    }
    Log.d("ExerciseListRoot", "ExerciseListRoot, uiState list size: ${uiState.exerciseList.size}")

    BodyTrackTheme {
        ExerciseListPage(
            bodyPartSelected = bodyPart,
            uiState = uiState,
            onExerciseClicked = { exerciseId ->
                Log.d("ExerciseListRoot", "Clicked Exercise with ID $exerciseId, bodypart Passed: $bodyPart")
                onExerciseClicked(exerciseId)
            },
            onLoadMore = {
                bodyPageViewmodel.onEvent(BodyPageEvent.OnLoadMoreExercises)
            }
        )
    }
}

@Composable
fun ExerciseListPage(
    bodyPartSelected: String,
    uiState: BodyPageUiState,
    onExerciseClicked: (exerciseId: String) -> Unit,
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.hasMorePages, uiState.isLoadingMore) {
        snapshotFlow {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 3
        }.collect { nearEnd ->
            if (nearEnd && uiState.hasMorePages && !uiState.isLoadingMore) {
                onLoadMore()
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize()
            .background(black1),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = uiState.exerciseList,
            key = { exercise -> exercise.exerciseId }
        )  { exercise ->
            ExerciseListItem(
                exerciseListItem = exercise,
                onClick = {
                    onExerciseClicked(it.exerciseId)
                }
            )
        }

        if (uiState.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = white1)
                }
            }
        }
    }
}


@Composable
fun ExerciseListItem (
    exerciseListItem: ExerciseListResponse,
    onClick : (ExerciseListResponse) -> Unit,
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
                onClick(exerciseListItem)
                Log.d("exerciseListItem", "Clicked ${exerciseListItem.name}")
            }
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ){
                AsyncImage(
                    model = exerciseListItem.imageUrl,
                    contentDescription = exerciseListItem.name,
                    modifier = Modifier.size(100.dp),
                    alignment = Alignment.Center
                )
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = exerciseListItem.name,
                    modifier = Modifier.padding(start = 24.dp),
                )
            }
        }
    }


}

@Composable
@Preview
fun ExerciseListItemPreview() {
    ExerciseListItem(ExerciseListResponse.DEFAULT, {})
}
