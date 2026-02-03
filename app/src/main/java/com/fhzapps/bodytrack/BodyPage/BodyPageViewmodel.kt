package com.fhzapps.bodytrack.BodyPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.BodyParts.BodyPart
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

sealed interface BodyPageEvent {
    data class OnBodyPartSelected(val bodyPart: String) : BodyPageEvent
    // data object OnLoadMoreExercises : BodyPageEvent // uncomment for API pagination
}

data class BodyPageUiState(
    val currentBodyPart: String = BodyPart.DEFAULT.toString(),
    val exerciseList: List<MovementEntity> = emptyList(),
    val isLoading: Boolean = false,
    // Pagination fields — uncomment for API pagination
    // val currentPage: Int = 1,
    // val hasMorePages: Boolean = false,
    // val isLoadingMore: Boolean = false,
)

class BodyPageViewmodel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val TAG = "BodyPageViewmodel"

    private val _uiState = MutableStateFlow(BodyPageUiState())
    val uiState: StateFlow<BodyPageUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<BodyPageEvent>()
    private var collectJob: Job? = null

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

    private fun handleEvent(event: BodyPageEvent) {
        when (event) {
            is BodyPageEvent.OnBodyPartSelected -> {
                Log.d(TAG, "OnBodyPartSelected: ${event.bodyPart}")
                _uiState.update { it.copy(currentBodyPart = event.bodyPart, isLoading = true) }
                collectJob?.cancel()
                collectJob = viewModelScope.launch {
                    repository.getMovementsByBodyPart(event.bodyPart).collect { movements ->
                        _uiState.update {
                            it.copy(exerciseList = movements, isLoading = false)
                        }
                        Log.d(TAG, "State updated: exerciseList.size=${movements.size}")
                    }
                }
            }

            // API pagination handler — uncomment when plugging in a new exercise API
            //
            // is BodyPageEvent.OnLoadMoreExercises -> {
            //     val current = _uiState.value
            //     if (!current.hasMorePages || current.isLoadingMore) return
            //     val nextPage = current.currentPage + 1
            //     _uiState.update { it.copy(isLoadingMore = true) }
            //     val response = repository.getListOfExercisesForBodyPart(
            //         current.currentBodyPart,
            //         offset = (nextPage - 1) * 25
            //     )
            //     val newItems = response?.data?.toList() ?: emptyList()
            //     val metadata = response?.metadata
            //     _uiState.update {
            //         it.copy(
            //             exerciseList = it.exerciseList + newItems,
            //             isLoadingMore = false,
            //             currentPage = metadata?.currentPage ?: nextPage,
            //             hasMorePages = (metadata?.currentPage ?: nextPage) < (metadata?.totalPages ?: nextPage)
            //         )
            //     }
            // }
        }
    }
}
