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
package com.earlier.yma.data.preferences

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.stringPreferencesKey

interface PreferenceSerializer<T> {
    fun write(preferences: MutablePreferences, data: T)

    fun read(preferences: MutablePreferences): T
}

object UserPreferencesSerializer : PreferenceSerializer<UserPreferences> {
    override fun write(preferences: MutablePreferences, data: UserPreferences) {
        with(data) {
            SchoolSerializer.write(preferences, school)
        }
    }

    override fun read(preferences: MutablePreferences): UserPreferences {
        return preferences.run {
            UserPreferences(
                school = SchoolSerializer.read(this)
            )
        }
    }
}

object SchoolSerializer : PreferenceSerializer<UserPreferences.School> {
    override fun write(preferences: MutablePreferences, data: UserPreferences.School) {
        val schoolCodeKey = stringPreferencesKey("schoolCode")
        val schoolNameKey = stringPreferencesKey("schoolName")
        val schoolKindKey = stringPreferencesKey("schoolKind")
        val orgCodeKey = stringPreferencesKey("orgCode")
        val orgNameKey = stringPreferencesKey("orgName")

        with(data) {
            preferences[schoolCodeKey] = schoolCode
            preferences[schoolNameKey] = schoolName
            preferences[schoolKindKey] = schoolKind
            preferences[orgCodeKey] = orgCode
            preferences[orgNameKey] = orgName
        }
    }

    override fun read(preferences: MutablePreferences): UserPreferences.School {
        val schoolCodeKey = stringPreferencesKey("schoolCode")
        val schoolNameKey = stringPreferencesKey("schoolName")
        val schoolKindKey = stringPreferencesKey("schoolKind")
        val orgCodeKey = stringPreferencesKey("orgCode")
        val orgNameKey = stringPreferencesKey("orgName")

        return preferences.run {
            UserPreferences.School(
                schoolCode = get(schoolCodeKey) ?: "",
                schoolName = get(schoolNameKey) ?: "",
                schoolKind = get(schoolKindKey) ?: "",
                orgCode = get(orgCodeKey) ?: "",
                orgName = get(orgNameKey) ?: "",
            )
        }
    }
}
