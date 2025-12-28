package com.fhzapps.bodytrack.BodyPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.BodyParts.BodyPart
import com.fhzapps.bodytrack.BodyParts.MuscleGroup
import com.fhzapps.bodytrack.data.ExerciseRepository
import com.fhzapps.bodytrack.exercises.Movement
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
    val movementList: List<Movement> = emptyList(),
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
    }

    fun onEvent(event: BodyPageEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private suspend fun handleEvent(event: BodyPageEvent) {
        when(event) {
            is BodyPageEvent.OnBodyPartSelected -> {
                Log.d(TAG, "____Clicked body part ${event.bodyPart.muscleGroup.name}")

                _uiState.update { it.copy(currentBodyPart = event.bodyPart, isLoading = true) }

                val movements = repository.getAllExercisesForBodyPartApi(getSearchableMuscleGroup(event.bodyPart.muscleGroup))
                Log.d("BodyPageViewmodel", "Exercise list size: ${movements?.size}")

                _uiState.update {
                    it.copy(
                        movementList = movements ?: emptyList(),
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
            MuscleGroup.BICEPS,MuscleGroup.TRICEPS -> "upper arms"
            MuscleGroup.TRAPS -> "back"
            MuscleGroup.ABS, MuscleGroup.OBLIQUES -> "waist"
            MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS -> "upper legs"
            MuscleGroup.CALVES -> "lower legs"
            MuscleGroup.GLUTES -> "lower legs"
        }
    }

}
