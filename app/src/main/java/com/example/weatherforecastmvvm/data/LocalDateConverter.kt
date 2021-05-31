package com.example.weatherforecastmvvm.data

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

object LocalDateConverter {

    @TypeConverter
    @JvmStatic
    fun stringToDate(str: String?) = str?.let {
        LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
    }

    //    =dateTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)
    @TypeConverter
    @JvmStatic
    fun dateToString(dateTime: LocalDate?): String? {
        var date = dateTime?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
        return date
    }
}
