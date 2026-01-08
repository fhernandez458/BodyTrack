package com.fhzapps.bodytrack.BodyPage

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.BodyParts.BodyPart
import com.fhzapps.bodytrack.BodyParts.MuscleGroup
import com.fhzapps.bodytrack.data.ExerciseListResponse
import com.fhzapps.bodytrack.data.ExerciseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface BodyPageEvent {
    data class OnBodyPartSelected(val bodyPart: BodyPart) : BodyPageEvent
}

data class BodyPageUiState(
    val currentBodyPart: BodyPart = BodyPart.DEFAULT,
    val exerciseList: List<ExerciseListResponse> = emptyList(),
    val isLoading: Boolean = false
)

class BodyPageViewmodel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val TAG = "BodyPageViewmodel"

    private val _uiState = MutableStateFlow(BodyPageUiState())
    val uiState: StateFlow<BodyPageUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<BodyPageEvent>()

    init {
        viewModelScope.launch {
            eventChannel.receiveAsFlow().collect { event ->
                handleEvent(event)
            }
        }

        viewModelScope.launch {
            _uiState.collect { newState ->
                Log.d(TAG, "_____ Backing _UI STATE UPDATED: $newState")
            }

        }
        viewModelScope.launch {

            uiState.collect { newState ->
                Log.d(TAG, "_____public UI STATE UPDATED: $newState")
            }
        }
    }

    fun onEvent(event: BodyPageEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private suspend fun handleEvent(event: BodyPageEvent) {
        when(event) {
            is BodyPageEvent.OnBodyPartSelected -> {
//                Log.d(TAG, "____Clicked body part ${event.bodyPart.muscleGroup.name}")
                _uiState.update { it.copy(currentBodyPart = event.bodyPart, isLoading = true) }
                val newExerciseList = repository.getListOfExercisesForBodyPart(getSearchableMuscleGroup(event.bodyPart.muscleGroup))
                val safeList = newExerciseList?.toList() ?: emptyList()
                _uiState.update {
                    it.copy(
                        exerciseList = safeList,
                        isLoading = false
                    )
                }

            }
        }
    }

    fun getSearchableMuscleGroup(selectedGroup: MuscleGroup): String{
        return when (selectedGroup) {
            MuscleGroup.SHOULDERS -> "shoulders"
            MuscleGroup.CHEST -> "chest"
            MuscleGroup.BICEPS -> "biceps"
            MuscleGroup.TRICEPS -> "triceps"
            MuscleGroup.TRAPS -> "back"
            MuscleGroup.ABS -> "abdominal"
            MuscleGroup.OBLIQUES -> "oblique"
            MuscleGroup.QUADS -> "quads"
            MuscleGroup.HAMSTRINGS -> "hamstrings"
            MuscleGroup.CALVES -> "calf"
            MuscleGroup.GLUTES -> "glute"
        }
    }

}
