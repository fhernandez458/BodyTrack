package com.fhzapps.bodytrack.exercises

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import com.fhzapps.bodytrack.BodyParts.BodyPart
import com.fhzapps.bodytrack.exercises.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExerciseViewModel(

) : ViewModel() {
  private val exercise: Exercise = Exercise.DEFAULT

  private val _sets = MutableStateFlow(3) // Initial value is 3
  val sets = _sets.asStateFlow()

  val api =

  suspend fun getSelectedExercise(exerciseID: String): Exercise {

    return exercise
  }

  fun getAllExercisesForBodyPart(bodyPart: BodyPart) : List<Exercise> {
    return listOf(Exercise.DEFAULT)

  }
    //populate exercise with data from repository


    //when leaving page ( onBackButtonPressed ) save the new mins and maxes to personal database




}