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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface PreferenceStorage {
    suspend fun writePreference(model: UserPreferences)

    fun readPreference(): Flow<UserPreferences>

    suspend fun readPreferenceOnce(): UserPreferences

    suspend fun clearStorage()
}

class AppPreferenceStorage(val dataStore: DataStore<Preferences>) : PreferenceStorage {
    override suspend fun writePreference(model: UserPreferences) {
        dataStore.edit {
            UserPreferencesAdapter.write(it, model)
        }
    }

    override fun readPreference(): Flow<UserPreferences> {
        return dataStore.data.map {
            UserPreferencesAdapter.read(it as MutablePreferences)
        }
    }

    override suspend fun readPreferenceOnce(): UserPreferences {
        return UserPreferencesAdapter.read(dataStore.data.first() as MutablePreferences)
    }

    override suspend fun clearStorage() {
        dataStore.edit {
            it.clear()
        }
    }
}
