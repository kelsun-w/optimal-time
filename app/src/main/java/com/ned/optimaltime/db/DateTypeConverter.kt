package com.ned.optimaltime.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class DateTypeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromTimeStamp(value: Long?): LocalDateTime? {
            return if (value == null)
                null
            else {
                Instant.ofEpochSecond(value).atZone(ZoneId.systemDefault()).toLocalDateTime()
            }
        }

        @TypeConverter
        @JvmStatic
        fun toTimeStamp(date: LocalDateTime): Long {
            return date.atZone(ZoneId.systemDefault()).toEpochSecond()
        }
    }
}