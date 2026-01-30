package com.fhzapps.bodytrack.BodyPage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fhzapps.bodytrack.BodyParts.BodyPart
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme
import com.fhzapps.bodytrack.ui.theme.black1


@Composable
fun BodyPageListViewRoot(
    onBodyPartClicked: (bodyPart: BodyPart) -> Unit,
) {
    BodyPageListView(
        onBodyPartClicked = { bodyPart ->
            onBodyPartClicked(bodyPart)
        }
    )
}


@Composable
fun BodyPageListView (
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onBodyPartClicked: (bodyPart: BodyPart) -> Unit,
) {
    Log.d("BodyPageListView", "BodyPageListView")
    BodyTrackTheme {
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(black1),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items(BodyPart.allBodyParts.size) { index ->
                BodyPartListItem(
                    bodyPart = BodyPart.allBodyParts[index],
                    onClick = {
                        onBodyPartClicked(BodyPart.allBodyParts[index])
                    }
                 )
            }
        }
    }
}