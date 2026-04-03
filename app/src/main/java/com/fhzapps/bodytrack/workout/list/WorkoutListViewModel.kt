package com.fhzapps.bodytrack.workout.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.data.WorkoutRepository
import com.fhzapps.bodytrack.data.workout.WorkoutEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface WorkoutListEvent {
    data object OnRefresh : WorkoutListEvent
    data class OnDeleteWorkout(val workoutId: Long) : WorkoutListEvent
    data class OnStartWorkout(val workoutId: Long) : WorkoutListEvent
    data object ClearStartedSession : WorkoutListEvent
}

data class WorkoutListUiState(
    val workouts: List<WorkoutEntity> = emptyList(),
    val isLoading: Boolean = true,
    val startedSessionId: Long? = null,
    val error: String? = null,
)

class WorkoutListViewModel(
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutListUiState())
    val uiState: StateFlow<WorkoutListUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<WorkoutListEvent>()

    init {
        viewModelScope.launch {
            eventChannel.receiveAsFlow().collect { event ->
                handleEvent(event)
            }
        }
        loadWorkouts()
    }

    fun onEvent(event: WorkoutListEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            workoutRepository.getAllWorkouts().collect { workouts ->
                _uiState.update {
                    it.copy(workouts = workouts, isLoading = false)
                }
            }
        }
    }

    private fun handleEvent(event: WorkoutListEvent) {
        when (event) {
            is WorkoutListEvent.OnStartWorkout -> {
                viewModelScope.launch {
                    try {
                        val sessionId = workoutRepository.startSession(event.workoutId)
                        _uiState.update { it.copy(startedSessionId = sessionId) }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(error = e.message) }
                    }
                }
            }

            is WorkoutListEvent.OnDeleteWorkout -> {
                viewModelScope.launch {
                    workoutRepository.deleteWorkout(event.workoutId)
                }
            }

            WorkoutListEvent.OnRefresh -> {
                _uiState.update { it.copy(isLoading = true) }
                loadWorkouts()
            }

            WorkoutListEvent.ClearStartedSession -> {
                _uiState.update { it.copy(startedSessionId = null) }
            }
        }
    }
}
