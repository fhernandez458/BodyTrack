package com.fhzapps.bodytrack.workout.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.BodyParts.MuscleGroup
import com.fhzapps.bodytrack.data.ExerciseRepository
import com.fhzapps.bodytrack.data.MovementEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ExercisePickerEvent {
    data class OnBodyPartSelected(val bodyPart: String) : ExercisePickerEvent
    data class OnExerciseToggled(val exerciseId: String) : ExercisePickerEvent
    data class OnSearchQueryChanged(val query: String) : ExercisePickerEvent
    data object OnConfirmSelection : ExercisePickerEvent
    data object OnShowAddExerciseDialog : ExercisePickerEvent
    data object OnDismissAddExerciseDialog : ExercisePickerEvent
    data class OnCreateExercise(
        val name: String,
        val bodyPart: String,
        val instructions: String,
        val equipment: List<String>,
        val targetMuscles: List<String>,
    ) : ExercisePickerEvent
}

data class ExercisePickerUiState(
    val bodyParts: List<String> = MuscleGroup.entries.map { it.name },
    val selectedBodyPart: String = "",
    val exercises: List<MovementEntity> = emptyList(),
    val selectedExerciseIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isConfirmed: Boolean = false,
    val showAddExerciseDialog: Boolean = false,
    val addExerciseError: String? = null,
)

class ExercisePickerViewModel(
    private val exerciseRepository: ExerciseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExercisePickerUiState())
    val uiState: StateFlow<ExercisePickerUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<ExercisePickerEvent>()
    private var collectJob: Job? = null

    init {
        viewModelScope.launch {
            eventChannel.receiveAsFlow().collect { event ->
                handleEvent(event)
            }
        }
    }

    fun onEvent(event: ExercisePickerEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private fun handleEvent(event: ExercisePickerEvent) {
        when (event) {
            is ExercisePickerEvent.OnBodyPartSelected -> {
                _uiState.update {
                    it.copy(
                        selectedBodyPart = event.bodyPart,
                        isLoading = true,
                    )
                }
                loadExercisesForBodyPart(event.bodyPart)
            }

            is ExercisePickerEvent.OnExerciseToggled -> {
                _uiState.update { state ->
                    val newSelection = if (event.exerciseId in state.selectedExerciseIds) {
                        state.selectedExerciseIds - event.exerciseId
                    } else {
                        state.selectedExerciseIds + event.exerciseId
                    }
                    state.copy(selectedExerciseIds = newSelection)
                }
            }

            is ExercisePickerEvent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }

            ExercisePickerEvent.OnConfirmSelection -> {
                _uiState.update { it.copy(isConfirmed = true) }
            }

            ExercisePickerEvent.OnShowAddExerciseDialog -> {
                _uiState.update { it.copy(showAddExerciseDialog = true, addExerciseError = null) }
            }

            ExercisePickerEvent.OnDismissAddExerciseDialog -> {
                _uiState.update { it.copy(showAddExerciseDialog = false, addExerciseError = null) }
            }

            is ExercisePickerEvent.OnCreateExercise -> {
                createExercise(event)
            }
        }
    }

    private fun createExercise(event: ExercisePickerEvent.OnCreateExercise) {
        viewModelScope.launch {
            if (exerciseRepository.exerciseNameExists(event.name)) {
                _uiState.update {
                    it.copy(addExerciseError = "An exercise with this name already exists")
                }
                return@launch
            }

            val newExercise = exerciseRepository.createExercise(
                name = event.name,
                bodyPart = event.bodyPart,
                instructions = event.instructions,
                equipment = event.equipment,
                targetMuscles = event.targetMuscles,
            )

            _uiState.update {
                it.copy(
                    showAddExerciseDialog = false,
                    addExerciseError = null,
                    selectedBodyPart = event.bodyPart,
                    selectedExerciseIds = it.selectedExerciseIds + newExercise.exerciseId,
                )
            }

            // Reload exercises for the body part
            loadExercisesForBodyPart(event.bodyPart)
        }
    }

    private fun loadExercisesForBodyPart(bodyPart: String) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            exerciseRepository.getMovementsByBodyPart(bodyPart).collect { exercises ->
                _uiState.update {
                    it.copy(
                        exercises = exercises,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun getSelectedExerciseIds(): List<String> = _uiState.value.selectedExerciseIds.toList()
}
