package com.fhzapps.bodytrack.workout.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.data.ExerciseRepository
import com.fhzapps.bodytrack.data.MovementEntity
import com.fhzapps.bodytrack.data.WorkoutRepository
import com.fhzapps.bodytrack.data.workout.WorkoutEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CreateWorkoutEvent {
    data class OnLoadWorkout(val workoutId: Long) : CreateWorkoutEvent
    data class OnNameChanged(val name: String) : CreateWorkoutEvent
    data class OnDescriptionChanged(val description: String) : CreateWorkoutEvent
    data class OnExercisesSelected(val exerciseIds: List<String>) : CreateWorkoutEvent
    data class OnExerciseRemoved(val exerciseId: String) : CreateWorkoutEvent
    data class OnExercisesReordered(val exerciseIds: List<String>) : CreateWorkoutEvent
    data object OnSave : CreateWorkoutEvent
}

data class CreateWorkoutUiState(
    val workoutId: Long? = null,
    val name: String = "",
    val description: String = "",
    val selectedExercises: List<MovementEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val nameError: String? = null,
)

class CreateWorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateWorkoutUiState())
    val uiState: StateFlow<CreateWorkoutUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<CreateWorkoutEvent>()

    init {
        viewModelScope.launch {
            eventChannel.receiveAsFlow().collect { event ->
                handleEvent(event)
            }
        }
    }

    fun onEvent(event: CreateWorkoutEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private fun handleEvent(event: CreateWorkoutEvent) {
        when (event) {
            is CreateWorkoutEvent.OnLoadWorkout -> loadWorkout(event.workoutId)
            is CreateWorkoutEvent.OnNameChanged -> {
                _uiState.update {
                    it.copy(
                        name = event.name,
                        nameError = null,
                    )
                }
            }
            is CreateWorkoutEvent.OnDescriptionChanged -> {
                _uiState.update { it.copy(description = event.description) }
            }
            is CreateWorkoutEvent.OnExercisesSelected -> loadExercises(event.exerciseIds)
            is CreateWorkoutEvent.OnExerciseRemoved -> {
                _uiState.update { state ->
                    state.copy(
                        selectedExercises = state.selectedExercises.filter {
                            it.exerciseId != event.exerciseId
                        }
                    )
                }
            }
            is CreateWorkoutEvent.OnExercisesReordered -> reorderExercises(event.exerciseIds)
            CreateWorkoutEvent.OnSave -> saveWorkout()
        }
    }

    private fun loadWorkout(workoutId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val workout = workoutRepository.getWorkoutWithExercisesOnce(workoutId)
            workout?.let { w ->
                _uiState.update {
                    it.copy(
                        workoutId = workoutId,
                        name = w.workout.name,
                        description = w.workout.description,
                        selectedExercises = w.exercises,
                        isLoading = false,
                    )
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadExercises(exerciseIds: List<String>) {
        viewModelScope.launch {
            val currentExerciseIds = _uiState.value.selectedExercises.map { it.exerciseId }.toSet()
            val newIds = exerciseIds.filter { it !in currentExerciseIds }

            val newExercises = newIds.mapNotNull { exerciseId ->
                exerciseRepository.getMovementById(exerciseId)?.let { movement ->
                    MovementEntity(
                        exerciseId = movement.exerciseId,
                        name = movement.name,
                        bodyPart = movement.bodyPart,
                        instructions = movement.instructions,
                        targetSets = movement.targetSets,
                        targetReps = movement.targetReps,
                        lastMaxWeight = movement.lastMaxWeight,
                        lastMaxReps = movement.lastMaxReps,
                        isUserCreated = movement.isUserCreated,
                        targetMuscles = movement.targetMuscles,
                        secondaryMuscles = movement.secondaryMuscles,
                        equipment = movement.equipment,
                        targetAreas = movement.targetAreas,
                        gifUrl = movement.gifUrl,
                    )
                }
            }

            _uiState.update { state ->
                state.copy(selectedExercises = state.selectedExercises + newExercises)
            }
        }
    }

    private fun reorderExercises(exerciseIds: List<String>) {
        val currentExercises = _uiState.value.selectedExercises.associateBy { it.exerciseId }
        val reorderedExercises = exerciseIds.mapNotNull { currentExercises[it] }
        _uiState.update { it.copy(selectedExercises = reorderedExercises) }
    }

    private fun saveWorkout() {
        val currentState = _uiState.value

        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Workout name is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val workoutId = if (currentState.workoutId != null) {
                workoutRepository.updateWorkout(
                    WorkoutEntity(
                        id = currentState.workoutId,
                        name = currentState.name.trim(),
                        description = currentState.description.trim(),
                    )
                )
                currentState.workoutId
            } else {
                workoutRepository.createWorkout(
                    name = currentState.name.trim(),
                    description = currentState.description.trim(),
                )
            }

            workoutRepository.setWorkoutExercises(
                workoutId = workoutId,
                exerciseIds = currentState.selectedExercises.map { it.exerciseId },
            )

            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
