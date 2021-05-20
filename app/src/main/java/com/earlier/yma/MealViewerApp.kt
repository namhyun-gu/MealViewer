package com.earlier.yma

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp

val Context.appPrefDataStore by preferencesDataStore("app")

@HiltAndroidApp
class MealViewerApp : Application()