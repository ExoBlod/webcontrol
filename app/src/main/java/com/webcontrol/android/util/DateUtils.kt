package com.webcontrol.android.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getDate(pattern: String? = "yyyy-MM-dd"): String {
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.US)
        val date = Date()
        return dateFormat.format(date)
    }

    fun getDateFormat(date: String, pattern: String? = "dd-MM-yyyy"): String {
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.US)
        val dateAux: Date = dateFormat.parse(date)
        return dateFormat.format(dateAux)
    }

    fun getDateOfString(date: String, separator: String): String {

        return (date.subSequence(0, 4)).toString() + separator + date.subSequence(4, 6)
            .toString() + separator + date.subSequence(6, 8).toString()
    }
}