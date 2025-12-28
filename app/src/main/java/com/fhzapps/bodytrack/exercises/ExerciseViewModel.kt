package com.fhzapps.bodytrack.exercises

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.BodyParts.BodyPart
import com.fhzapps.bodytrack.data.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

  private val _movement = MutableStateFlow(Movement.DEFAULT)
    val exercise = _movement.asStateFlow()


  private val _sets = MutableStateFlow(3) // Initial value is 3
    val sets = _sets.asStateFlow()

    fun getSelectedExercise(exerciseID: String) {
        Log.d("ExerciseViewModel", "Getting exercise with ID: $exerciseID")
        viewModelScope.launch {
            _movement.value = repository.getExerciseByIdApi(exerciseID)?: Movement.DEFAULT
            Log.d("ExerciseViewModel", "Exercise retrieved: ${_movement.value.exerciseId}")

        }
   }

  fun getAllExercisesForBodyPart(bodyPart: BodyPart) : List<Movement> {

      return listOf(Movement.DEFAULT)

  }
    //populate exercise with data from repository


    //when leaving page ( onBackButtonPressed ) save the new mins and maxes to personal database




}