package com.andlill.keynotes.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataConverter {

    @TypeConverter
    @JvmStatic
    fun convertToJson(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun convertToStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>(){}.type
        return Gson().fromJson(value, type)
    }
}