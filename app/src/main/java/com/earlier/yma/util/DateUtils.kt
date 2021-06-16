/*
 * Copyright 2021 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.earlier.yma.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatDate(date: Date, pattern: String = "yyyyMMdd"): String {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return format.format(date)
    }

    fun convertDate(localDate: LocalDate): Date {
        val defaultZoneId = ZoneId.systemDefault()
        return Date.from(
            localDate.atStartOfDay(defaultZoneId).toInstant()
        )
    }

    fun convertLocalDate(date: Date): LocalDate {
        val defaultZoneId = ZoneId.systemDefault()
        return date.toInstant().atZone(defaultZoneId).toLocalDate()
    }
}
