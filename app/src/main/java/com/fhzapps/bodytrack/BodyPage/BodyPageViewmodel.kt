package com.fhzapps.bodytrack.BodyPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhzapps.bodytrack.BodyParts.BodyPart
import com.fhzapps.bodytrack.BodyParts.MuscleGroup
import com.fhzapps.bodytrack.data.ExerciseRepository
import com.fhzapps.bodytrack.exercises.Exercise
import kotlinx.coroutines.launch

class BodyPageViewmodel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val TAG = "BodyPageViewmodel"
    private var _currentBodyPart = BodyPart.DEFAULT
    val currentBodyPart: BodyPart
        get() = _currentBodyPart
    var exerciseList: MutableList<Exercise>? = mutableListOf()

    fun onListItemClicked(bodyPart: BodyPart) {
        Log.d(TAG, "____Clicked body part ${bodyPart.muscleGroup.name}")

        _currentBodyPart = bodyPart

        viewModelScope.launch {
           exerciseList = repository.getAllExercisesForBodyPartApi(getSearchableMuscleGroup(bodyPart.muscleGroup))
            Log.d("BodyPageViewmodel", "Exercise list size: ${exerciseList?.forEach { it.exerciseId }}")
        }

    }

    fun getSearchableMuscleGroup(selectedGroup: MuscleGroup): String{
        return when (selectedGroup) {
            MuscleGroup.SHOULDERS -> "shoulders"
            MuscleGroup.CHEST -> "chest"
            MuscleGroup.BICEPS,MuscleGroup.TRICEPS -> "upper arms"
            MuscleGroup.TRAPS -> "back"
            MuscleGroup.ABS, MuscleGroup.OBLIQUES -> "waist"
            MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS -> "upper legs"
            MuscleGroup.CALVES -> "lower legs"
            MuscleGroup.GLUTES -> "lower legs"
        }
    }

}