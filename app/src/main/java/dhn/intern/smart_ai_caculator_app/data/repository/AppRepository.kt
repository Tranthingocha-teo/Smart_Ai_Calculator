package dhn.intern.smart_ai_caculator_app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dhn.intern.smart_ai_caculator_app.data.source.menu_setting.AppKeys
import kotlinx.coroutines.flow.map

class AppRepository(
    private val dataStore: DataStore<Preferences>
) {

    val isFirstLaunchFlow = dataStore.data
        .map { prefs ->
            prefs[AppKeys.IS_FIRST_LAUNCH] ?: true
        }

    suspend fun setFirstLaunchDone() {
        dataStore.edit {
            it[AppKeys.IS_FIRST_LAUNCH] = false
        }
    }
}