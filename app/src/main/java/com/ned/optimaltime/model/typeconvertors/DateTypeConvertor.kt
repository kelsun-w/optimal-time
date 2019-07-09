package com.ned.optimaltime.model.typeconvertors

import androidx.room.TypeConverter
import java.util.*

class DateTypeConvertor {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromTimeStamp(value: Long): Date? {
            return if (value == null)
                null
            else
                Date(value)
        }

        @TypeConverter
        @JvmStatic
        fun toTimeStamp(date: Date): Long {
            return date.time
        }
    }
}