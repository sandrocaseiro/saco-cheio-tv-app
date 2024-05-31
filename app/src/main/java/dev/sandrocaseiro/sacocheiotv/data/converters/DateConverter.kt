package dev.sandrocaseiro.sacocheiotv.data.converters

import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DateConverter {
    @TypeConverter
    fun stringToDate(value: String?): Date? {
        if (value == null)
            return null

        val df: DateFormat = SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault())

        try {
            return df.parse(value)
        } catch (e: ParseException) {
//            e.printStackTrace()
        }

        return null
    }

    @TypeConverter
    fun dateToString(value: Date?): String? {
        if (value == null)
            return null

        val df: DateFormat = SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault())

        try {
            return df.format(value)
        }
        catch (e: ParseException) {
            //            e.printStackTrace()
        }

        return null
    }

    companion object {
        private const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }
}