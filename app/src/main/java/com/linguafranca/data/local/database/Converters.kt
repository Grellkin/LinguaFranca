package com.linguafranca.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            gson.fromJson(it, object : TypeToken<List<String>>() {}.type)
        }
    }

    @TypeConverter
    fun fromExamplesMap(value: Map<String, String?>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toExamplesMap(value: String?): Map<String, String?>? {
        return value?.let {
            gson.fromJson(it, object : TypeToken<Map<String, String?>>() {}.type)
        }
    }
}

