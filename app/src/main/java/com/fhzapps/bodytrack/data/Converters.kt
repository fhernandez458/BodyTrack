package com.fhzapps.bodytrack.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return value?.let { Gson().fromJson(it, object : TypeToken<List<String>>() {}.type) }
    }

    @TypeConverter
    fun fromListString(list: List<String>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun weightUnitToString(unit: WeightUnit): String {
        return unit.toString()
    }

    @TypeConverter
    fun stringToWeightUnit(unitString: String): WeightUnit {
        return if (unitString == "kg") WeightUnit.KG else WeightUnit.LBS
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}
