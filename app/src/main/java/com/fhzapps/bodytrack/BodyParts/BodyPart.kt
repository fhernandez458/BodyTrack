package com.fhzapps.bodytrack.BodyParts

import androidx.annotation.DrawableRes
import com.fhzapps.bodytrack.R
import com.fhzapps.bodytrack.exercises.Movement
import com.fhzapps.bodytrack.ui.theme.green1
import com.fhzapps.bodytrack.ui.theme.lightGreen
import com.fhzapps.bodytrack.ui.theme.lightRed
import com.fhzapps.bodytrack.ui.theme.red1

class BodyPart(
    val state: BodyPartState,
    val muscleGroup: MuscleGroup,
    val movements: List<Movement>,
) {
    val color = when (state) {
        BodyPartState.READY -> green1
        BodyPartState.RESTING -> lightGreen
        BodyPartState.FATIGUED -> lightRed
        BodyPartState.INJURED -> red1
    }
    @DrawableRes
    val image: Int =
        when (muscleGroup) {
            MuscleGroup.CHEST -> R.drawable.chest
            MuscleGroup.SHOULDERS -> R.drawable.shoulders
            MuscleGroup.TRAPS -> R.drawable.traps_front
            MuscleGroup.QUADS -> R.drawable.quads
            MuscleGroup.HAMSTRINGS -> R.drawable.hams
            MuscleGroup.CALVES -> R.drawable.calves
            MuscleGroup.GLUTES -> R.drawable.glutes
            MuscleGroup.TRICEPS -> R.drawable.triceps
            MuscleGroup.BICEPS -> R.drawable.biceps
            MuscleGroup.ABS -> R.drawable.abs
            MuscleGroup.OBLIQUES -> R.drawable.obliques
        }
    companion object {
        val DEFAULT = BodyPart(BodyPartState.READY, MuscleGroup.CHEST, listOf())

        // This property is initialized once and the same list is reused.
        val allBodyParts: List<BodyPart> = MuscleGroup.entries.map { muscleGroup ->
            BodyPart(BodyPartState.READY, muscleGroup, listOf())
        }
    }

}