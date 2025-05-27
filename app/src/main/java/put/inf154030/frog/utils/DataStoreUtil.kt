package put.inf154030.frog.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Extension property for DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "frog_preferences")