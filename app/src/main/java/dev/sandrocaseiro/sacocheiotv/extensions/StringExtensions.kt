package dev.sandrocaseiro.sacocheiotv.extensions

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun String.toDateFormat(format: String): Date {
    val df: DateFormat = SimpleDateFormat(format, Locale.getDefault())

    try {
        return df.parse(this)!!
    } catch (e: ParseException) {
//            e.printStackTrace()
    }

    return Date()
}

// TODO: Move to other file
fun Date.toStringFormat(format: String): String {
    val df: DateFormat = SimpleDateFormat(format, Locale.getDefault())

    try {
        return df.format(this)
    } catch (e: ParseException) {
//            e.printStackTrace()
    }

    return ""
}

// TODO: Move to other file

fun Long.toTimeString(): String {
    return String.format(
        Locale.getDefault(),
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this)),
        TimeUnit.MILLISECONDS.toSeconds(this) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
    )
}
