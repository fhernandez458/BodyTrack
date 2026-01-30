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
    data class OnBodyPartSelected(val bodyPart: String) : BodyPageEvent
    data object OnLoadMoreExercises : BodyPageEvent
}

data class BodyPageUiState(
    val currentBodyPart: String = BodyPart.DEFAULT.toString(),
    val exerciseList: List<ExerciseListResponse> = emptyList(),
    val isLoading: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = false,
    val isLoadingMore: Boolean = false
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
                Log.d(TAG, "OnBodyPartSelected: ${event.bodyPart}")
                _uiState.update { it.copy(currentBodyPart = event.bodyPart, isLoading = true, currentPage = 1) }
                val response = repository.getListOfExercisesForBodyPart(event.bodyPart, offset = 0)
                Log.d(TAG, "API response null=${response == null}, dataSize=${response?.data?.size}, metadata=${response?.metadata}")
                val safeList = response?.data?.toList() ?: emptyList()
                val metadata = response?.metadata
                _uiState.update {
                    it.copy(
                        exerciseList = safeList,
                        isLoading = false,
                        currentPage = metadata?.currentPage ?: 1,
                        hasMorePages = (metadata?.currentPage ?: 1) < (metadata?.totalPages ?: 1)
                    )
                }
                Log.d(TAG, "State updated: exerciseList.size=${_uiState.value.exerciseList.size}")
            }

            is BodyPageEvent.OnLoadMoreExercises -> {
                val current = _uiState.value
                if (!current.hasMorePages || current.isLoadingMore) return
                val nextPage = current.currentPage + 1
                _uiState.update { it.copy(isLoadingMore = true) }
                val response = repository.getListOfExercisesForBodyPart(
                    current.currentBodyPart,
                    offset = (nextPage - 1) * 25
                )
                val newItems = response?.data?.toList() ?: emptyList()
                val metadata = response?.metadata
                _uiState.update {
                    it.copy(
                        exerciseList = it.exerciseList + newItems,
                        isLoadingMore = false,
                        currentPage = metadata?.currentPage ?: nextPage,
                        hasMorePages = (metadata?.currentPage ?: nextPage) < (metadata?.totalPages ?: nextPage)
                    )
                }
            }
        }
    }

}
