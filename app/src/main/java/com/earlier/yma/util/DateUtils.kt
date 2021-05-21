package com.earlier.yma.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getToday(): String = formatDate(Date())

    fun formatDate(date: Date): String {
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return format.format(date)
    }
}