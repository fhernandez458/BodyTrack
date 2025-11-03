package com.fhzapps.bodytrack.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "set_data_table",
    // This foreign key establishes the link to ExerciseData
    foreignKeys = [
        ForeignKey(
            entity = ExerciseData::class,
            parentColumns = ["id"], // The Primary Key of ExerciseData
            childColumns = ["exerciseDataId"], // The foreign key in this table
            onDelete = ForeignKey.CASCADE // If an ExerciseData is deleted, delete its sets too
        )
    ]
)
data class SetData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val setNumber: Int,
    val reps: Int,
    val weight: Double,
    val unit: WeightUnit,
    val date: Date,
    // This is the crucial foreign key column that links this Set to an ExerciseData
    // It's indexed for faster queries.
    val exerciseDataId: Long,
)

enum class WeightUnit {
    KG,
    LBS
}