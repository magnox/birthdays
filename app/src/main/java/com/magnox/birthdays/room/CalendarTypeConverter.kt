package com.magnox.birthdays.room

import androidx.room.TypeConverter
import java.util.*

class CalendarTypeConverter {
    @TypeConverter
    fun timestampToCalendar(timestamp: Long?): Calendar? {
        return if (timestamp == null) null else Calendar.getInstance().also {
            it.timeInMillis = timestamp
        }
    }

    @TypeConverter
    fun calendarToTimestamp(cal: Calendar?): Long? {
        return cal?.timeInMillis
    }
}