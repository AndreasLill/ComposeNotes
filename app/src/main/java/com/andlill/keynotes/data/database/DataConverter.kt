package com.andlill.keynotes.data.database

import androidx.room.TypeConverter
import com.andlill.keynotes.model.Label
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataConverter {

    @TypeConverter
    @JvmStatic
    fun convertToJson(value: List<Label>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun convertToLabelList(value: String): List<Label> {
        val type = object : TypeToken<List<Label>>(){}.type
        return Gson().fromJson(value, type)
    }
}