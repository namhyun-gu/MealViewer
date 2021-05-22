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
package com.earlier.yma.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class PreferenceModel(
    val schoolCode: String,
    val schoolName: String,
    val schoolKind: String,
    val orgCode: String,
    val orgName: String,
) {
    fun write(preferences: MutablePreferences) {
        val schoolCodeKey = stringPreferencesKey("schoolCode")
        val schoolNameKey = stringPreferencesKey("schoolName")
        val schoolKindKey = stringPreferencesKey("schoolKind")
        val orgCodeKey = stringPreferencesKey("orgCode")
        val orgNameKey = stringPreferencesKey("orgName")

        preferences[schoolCodeKey] = schoolCode
        preferences[schoolNameKey] = schoolName
        preferences[schoolKindKey] = schoolKind
        preferences[orgCodeKey] = orgCode
        preferences[orgNameKey] = orgName
    }

    companion object {
        fun read(preferences: Preferences): PreferenceModel {
            val schoolCodeKey = stringPreferencesKey("schoolCode")
            val schoolNameKey = stringPreferencesKey("schoolName")
            val schoolKindKey = stringPreferencesKey("schoolKind")
            val orgCodeKey = stringPreferencesKey("orgCode")
            val orgNameKey = stringPreferencesKey("orgName")

            return PreferenceModel(
                schoolCode = preferences[schoolCodeKey] ?: "",
                schoolName = preferences[schoolNameKey] ?: "",
                schoolKind = preferences[schoolKindKey] ?: "",
                orgCode = preferences[orgCodeKey] ?: "",
                orgName = preferences[orgNameKey] ?: "",
            )
        }
    }
}

interface PreferenceStorage {
    suspend fun writePreference(model: PreferenceModel)

    fun readPreference(): Flow<PreferenceModel>

    suspend fun readPreferenceOnce(): PreferenceModel

    suspend fun clearStorage()
}

class AppPreferenceStorage(val dataStore: DataStore<Preferences>) : PreferenceStorage {
    override suspend fun writePreference(model: PreferenceModel) {
        dataStore.edit {
            model.write(it)
        }
    }

    override fun readPreference(): Flow<PreferenceModel> {
        return dataStore.data.map { PreferenceModel.read(it) }
    }

    override suspend fun readPreferenceOnce(): PreferenceModel {
        return PreferenceModel.read(dataStore.data.first())
    }

    override suspend fun clearStorage() {
        dataStore.edit {
            it.clear()
        }
    }
}
