package com.fhzapps.bodytrack.workout.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fhzapps.bodytrack.BodyParts.MuscleGroup
import com.fhzapps.bodytrack.ui.theme.black1
import com.fhzapps.bodytrack.ui.theme.darkGray
import com.fhzapps.bodytrack.ui.theme.green1
import com.fhzapps.bodytrack.ui.theme.lightGray
import com.fhzapps.bodytrack.ui.theme.red1
import com.fhzapps.bodytrack.ui.theme.white1

data class NewExerciseData(
    val name: String,
    val bodyPart: String,
    val instructions: String,
    val equipment: List<String>,
    val targetMuscles: List<String>,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExerciseDialog(
    initialBodyPart: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (NewExerciseData) -> Unit,
    nameError: String? = null,
) {
    var name by remember { mutableStateOf("") }
    var selectedBodyPart by remember { mutableStateOf(initialBodyPart ?: "") }
    var instructions by remember { mutableStateOf("") }
    var equipmentText by remember { mutableStateOf("") }
    var targetMusclesText by remember { mutableStateOf("") }

    val bodyParts = MuscleGroup.entries.map { it.name }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Custom Exercise",
                color = white1,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name *") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = red1) } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Body part selection
                Text(
                    text = "Body Part *",
                    style = MaterialTheme.typography.bodyMedium,
                    color = lightGray,
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    bodyParts.forEach { bodyPart ->
                        FilterChip(
                            selected = bodyPart == selectedBodyPart,
                            onClick = { selectedBodyPart = bodyPart },
                            label = {
                                Text(
                                    text = bodyPart.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (bodyPart == selectedBodyPart) black1 else white1,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = black1,
                                selectedContainerColor = green1,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Instructions field
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    minLines = 2,
                    maxLines = 4,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Equipment field
                OutlinedTextField(
                    value = equipmentText,
                    onValueChange = { equipmentText = it },
                    label = { Text("Equipment (comma-separated)") },
                    placeholder = { Text("e.g., Barbell, Dumbbells") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Target muscles field
                OutlinedTextField(
                    value = targetMusclesText,
                    onValueChange = { targetMusclesText = it },
                    label = { Text("Target Muscles (comma-separated)") },
                    placeholder = { Text("e.g., Chest, Triceps") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val equipment = equipmentText
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    val targetMuscles = targetMusclesText
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    onConfirm(
                        NewExerciseData(
                            name = name.trim(),
                            bodyPart = selectedBodyPart,
                            instructions = instructions.trim(),
                            equipment = equipment,
                            targetMuscles = targetMuscles,
                        )
                    )
                },
                enabled = name.isNotBlank() && selectedBodyPart.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = green1),
            ) {
                Text("Add Exercise")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = lightGray)
            }
        },
        containerColor = darkGray,
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = white1,
    unfocusedTextColor = white1,
    focusedBorderColor = green1,
    unfocusedBorderColor = lightGray,
    focusedLabelColor = green1,
    unfocusedLabelColor = lightGray,
    cursorColor = white1,
    focusedPlaceholderColor = lightGray,
    unfocusedPlaceholderColor = lightGray,
)
