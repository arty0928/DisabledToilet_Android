package com.dream.disabledtoilet_android.Utility.Database
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",") // List를 String으로 변환
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",") ?: emptyList() // String을 List로 변환
    }
}