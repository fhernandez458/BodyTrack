package com.fhzapps.bodytrack.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * This class represents the one-to-many relationship between ExerciseData and SetData.
 * It is used for querying, NOT for storing in the database.
 */
data class ExerciseDataWithSets(
    // @Embedded tells Room to treat the fields of ExerciseData as if they were part of this class
    @Embedded
    val exercise: ExerciseData,

    // @Relation tells Room to fetch the list of SetData objects
    @Relation(
        parentColumn = "id",           // The primary key of the parent entity (ExerciseData)
        entityColumn = "exerciseDataId" // The foreign key in the child entity (SetData)
    )
    val sets: List<SetData>
)