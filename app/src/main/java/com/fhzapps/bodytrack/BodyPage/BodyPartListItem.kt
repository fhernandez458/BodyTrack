package com.fhzapps.bodytrack.BodyPage

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fhzapps.bodytrack.BodyParts.BodyPart
import com.fhzapps.bodytrack.BodyParts.BodyPartState
import com.fhzapps.bodytrack.BodyParts.MuscleGroup
import com.fhzapps.bodytrack.R
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme
import com.fhzapps.bodytrack.ui.theme.darkGray
import com.fhzapps.bodytrack.ui.theme.lightGray
import com.fhzapps.bodytrack.ui.theme.white1

@Composable
fun BodyPartListItem(
    bodyPart: BodyPart,
    onClick: (BodyPart) -> Unit
) {
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
        onClick = { onClick(bodyPart)
        Log.d("BodyPartListItem", "Clicked ${bodyPart.muscleGroup.name}")
    }
    ){
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            ){
            Image(
                painter = painterResource(id = bodyPart.image),
                contentDescription = bodyPart.muscleGroup.name,
                modifier = Modifier.size(100.dp)
            )
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = bodyPart.muscleGroup.name,
                modifier = Modifier.padding(start = 24.dp),
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun BodyPartListItemPreview() {
    BodyTrackTheme{
        BodyPartListItem(
            bodyPart = BodyPart(
                state = BodyPartState.READY,
                muscleGroup = MuscleGroup.CHEST,
                exercises = listOf()
            ),
            onClick = { }
        )
    }

}