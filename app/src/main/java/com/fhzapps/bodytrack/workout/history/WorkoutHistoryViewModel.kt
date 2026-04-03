package com.fhzapps.bodytrack.workout.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.data.WorkoutRepository
import com.fhzapps.bodytrack.data.workout.WorkoutSessionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutHistoryUiState(
    val sessions: List<WorkoutSessionEntity> = emptyList(),
    val isLoading: Boolean = true,
)

class WorkoutHistoryViewModel(
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            workoutRepository.getAllSessions().collect { sessions ->
                _uiState.update {
                    it.copy(
                        sessions = sessions.filter { session -> session.isCompleted },
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            workoutRepository.deleteSession(sessionId)
        }
    }
}
