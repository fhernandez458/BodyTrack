package com.fhzapps.bodytrack.workout.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.data.WeightUnit
import com.fhzapps.bodytrack.data.WorkoutRepository
import com.fhzapps.bodytrack.data.workout.SessionExerciseWithSets
import com.fhzapps.bodytrack.data.workout.SessionSetEntity
import com.fhzapps.bodytrack.data.workout.WorkoutSessionEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ActiveSessionEvent {
    data class OnLoadSession(val sessionId: Long) : ActiveSessionEvent
    data class OnLogSet(
        val sessionExerciseId: Long,
        val weight: Double,
        val reps: Int,
        val unit: WeightUnit,
    ) : ActiveSessionEvent
    data class OnDeleteSet(val setId: Long) : ActiveSessionEvent
    data class OnCompleteExercise(val sessionExerciseId: Long) : ActiveSessionEvent
    data class OnNavigateToExercise(val index: Int) : ActiveSessionEvent
    data class OnCompleteSession(val notes: String) : ActiveSessionEvent
    data object OnDiscardSession : ActiveSessionEvent
}

data class ActiveSessionUiState(
    val session: WorkoutSessionEntity? = null,
    val exercises: List<SessionExerciseWithSets> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val isLoading: Boolean = true,
    val isCompleted: Boolean = false,
    val isDiscarded: Boolean = false,
    val elapsedSeconds: Long = 0,
)

class ActiveSessionViewModel(
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveSessionUiState())
    val uiState: StateFlow<ActiveSessionUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<ActiveSessionEvent>()
    private var timerJob: Job? = null
    private var collectJob: Job? = null

    init {
        viewModelScope.launch {
            eventChannel.receiveAsFlow().collect { event ->
                handleEvent(event)
            }
        }
    }

    fun onEvent(event: ActiveSessionEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private fun handleEvent(event: ActiveSessionEvent) {
        when (event) {
            is ActiveSessionEvent.OnLoadSession -> loadSession(event.sessionId)
            is ActiveSessionEvent.OnLogSet -> logSet(event)
            is ActiveSessionEvent.OnDeleteSet -> deleteSet(event.setId)
            is ActiveSessionEvent.OnCompleteExercise -> completeExercise(event.sessionExerciseId)
            is ActiveSessionEvent.OnNavigateToExercise -> {
                _uiState.update { it.copy(currentExerciseIndex = event.index) }
            }
            is ActiveSessionEvent.OnCompleteSession -> completeSession(event.notes)
            ActiveSessionEvent.OnDiscardSession -> discardSession()
        }
    }

    private fun loadSession(sessionId: Long) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            workoutRepository.getSessionWithDetails(sessionId).collect { sessionWithDetails ->
                sessionWithDetails?.let { details ->
                    _uiState.update {
                        it.copy(
                            session = details.session,
                            exercises = details.exercises.sortedBy { ex -> ex.sessionExercise.orderIndex },
                            isLoading = false,
                        )
                    }

                    if (timerJob == null && !details.session.isCompleted) {
                        startTimer(details.session.startTime)
                    }
                }
            }
        }
    }

    private fun startTimer(startTime: Long) {
        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                _uiState.update { it.copy(elapsedSeconds = elapsed) }
                delay(1000)
            }
        }
    }

    private fun logSet(event: ActiveSessionEvent.OnLogSet) {
        viewModelScope.launch {
            val currentExercise = _uiState.value.exercises
                .find { it.sessionExercise.id == event.sessionExerciseId }
            val nextSetNumber = (currentExercise?.sets?.maxOfOrNull { it.setNumber } ?: 0) + 1

            workoutRepository.logSet(
                sessionExerciseId = event.sessionExerciseId,
                setNumber = nextSetNumber,
                weight = event.weight,
                reps = event.reps,
                unit = event.unit,
            )
        }
    }

    private fun deleteSet(setId: Long) {
        viewModelScope.launch {
            workoutRepository.deleteSet(setId)
        }
    }

    private fun completeExercise(sessionExerciseId: Long) {
        viewModelScope.launch {
            val exercise = _uiState.value.exercises
                .find { it.sessionExercise.id == sessionExerciseId }
                ?.sessionExercise ?: return@launch

            workoutRepository.updateSessionExercise(
                exercise.copy(isCompleted = true)
            )

            // Move to next exercise if available
            val currentIndex = _uiState.value.currentExerciseIndex
            val totalExercises = _uiState.value.exercises.size
            if (currentIndex < totalExercises - 1) {
                _uiState.update { it.copy(currentExerciseIndex = currentIndex + 1) }
            }
        }
    }

    private fun completeSession(notes: String) {
        viewModelScope.launch {
            val sessionId = _uiState.value.session?.id ?: return@launch
            workoutRepository.completeSession(sessionId, notes)
            timerJob?.cancel()
            _uiState.update { it.copy(isCompleted = true) }
        }
    }

    private fun discardSession() {
        viewModelScope.launch {
            val sessionId = _uiState.value.session?.id ?: return@launch
            workoutRepository.deleteSession(sessionId)
            timerJob?.cancel()
            _uiState.update { it.copy(isDiscarded = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        collectJob?.cancel()
    }
}
