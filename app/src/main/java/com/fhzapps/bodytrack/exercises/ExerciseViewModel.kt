package com.fhzapps.bodytrack.exercises

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.BodyParts.BodyPart
import com.fhzapps.bodytrack.data.ExerciseRepository
import com.fhzapps.bodytrack.data.ExerciseRepositoryImpl
import com.fhzapps.bodytrack.exercises.Exercise
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlin.system.exitProcess

class ExerciseViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

  private val _exercise = MutableStateFlow(Exercise.DEFAULT)
    val exercise = _exercise.asStateFlow()


  private val _sets = MutableStateFlow(3) // Initial value is 3
    val sets = _sets.asStateFlow()

    fun getSelectedExercise(exerciseID: String) {
        Log.d("ExerciseViewModel", "Getting exercise with ID: $exerciseID")
        viewModelScope.launch {
            _exercise.value = repository.getExerciseByIdApi(exerciseID)?: Exercise.DEFAULT
            Log.d("ExerciseViewModel", "Exercise retrieved: ${_exercise.value.exerciseId}")

        }
   }

  fun getAllExercisesForBodyPart(bodyPart: BodyPart) : List<Exercise> {

      return listOf(Exercise.DEFAULT)

  }
    //populate exercise with data from repository


    //when leaving page ( onBackButtonPressed ) save the new mins and maxes to personal database




}