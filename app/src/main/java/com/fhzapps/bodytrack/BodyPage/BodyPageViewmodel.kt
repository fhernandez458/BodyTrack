package com.fhzapps.bodytrack.BodyPage

import android.util.Log
import androidx.lifecycle.ViewModel
import com.fhzapps.bodytrack.BodyParts.BodyPart

class BodyPageViewmodel(

) : ViewModel() {
    private val TAG = "BodyPageViewmodel"
    private var _currentBodyPart = BodyPart.DEFAULT
    val currentBodyPart: BodyPart
        get() = _currentBodyPart

    fun onListItemClicked(bodyPart: BodyPart) {
        _currentBodyPart = bodyPart
        Log.d(TAG, "Clicked body part ${bodyPart.muscleGroup.name}")
    }

}