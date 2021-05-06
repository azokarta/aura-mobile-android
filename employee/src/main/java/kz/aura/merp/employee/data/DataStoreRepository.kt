package kz.aura.merp.employee.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_POSITION_ID
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_TOKEN
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_USERNAME
import kz.aura.merp.employee.model.Salary
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val PREFERENCES_TOKEN = stringPreferencesKey("token")
        val PREFERENCES_POSITION_ID = longPreferencesKey("positionId")
        val PREFERENCES_USERNAME = stringPreferencesKey("username")
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    val tokenFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PREFERENCES_TOKEN] ?: ""
        }

    val salaryFlow: Flow<Salary> = dataStore.data
        .map { preferences ->
            val positionId = preferences[PREFERENCES_POSITION_ID] ?: 0
            val username = preferences[PREFERENCES_USERNAME] ?: ""
            Salary(positionId = positionId, username = username)
        }

    suspend fun saveToken(token: String) {
        dataStore.edit { settings ->
            settings[PREFERENCES_TOKEN] = token
        }
    }

    suspend fun saveSalary(salary: Salary) {
        dataStore.edit { settings ->
            settings[PREFERENCES_POSITION_ID] = salary.positionId!!
            settings[PREFERENCES_USERNAME] = salary.username!!
        }
    }

}