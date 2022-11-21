package com.andlill.composenotes.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object PreferencesKeys {
    val IS_GRID_VIEW = booleanPreferencesKey("is_grid_view")
    val NOTE_PREVIEW_MAX_LINES = intPreferencesKey("note_preview_max_lines")
}