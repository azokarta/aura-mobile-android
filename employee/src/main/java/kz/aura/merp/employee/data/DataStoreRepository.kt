package kz.aura.merp.employee.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_COUNTRY_CALLING_CODE
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_PASS_CODE
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_POSITION_ID
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_TOKEN
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_USERNAME
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.util.CountryCode
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val PREFERENCES_TOKEN = stringPreferencesKey("token")
        val PREFERENCES_POSITION_ID = longPreferencesKey("positionId")
        val PREFERENCES_USERNAME = stringPreferencesKey("username")
        val PREFERENCES_PASS_CODE = stringPreferencesKey("passcode")
        val PREFERENCES_COUNTRY_CALLING_CODE = stringPreferencesKey("countryCallingCode")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val dataStore: DataStore<Preferences> = context.dataStore

    val tokenFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PREFERENCES_TOKEN] ?: ""
        }

    val salaryFlow: Flow<Salary?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val positionId = preferences[PREFERENCES_POSITION_ID] ?: 0
            val username = preferences[PREFERENCES_USERNAME] ?: ""
            if (positionId != 0L && username.isNotBlank()) {
                Salary(positionId = positionId, username = username)
            } else null
        }
    val passCodeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PREFERENCES_PASS_CODE] ?: ""
        }
    val countryCodeFlow: Flow<CountryCode> = dataStore.data
        .map { preferences ->
            val countryCallingCode = preferences[PREFERENCES_COUNTRY_CALLING_CODE] ?: "+7"
            CountryCode.values().find { it.phoneCode == countryCallingCode }!!
        }

    suspend fun saveToken(token: String) {
        dataStore.edit { settings ->
            settings[PREFERENCES_TOKEN] = token
        }
    }

    suspend fun saveCountryCallingCode(countryCallingCode: String) {
        dataStore.edit { settings ->
            settings[PREFERENCES_COUNTRY_CALLING_CODE] = countryCallingCode
        }
    }

    suspend fun saveSalary(salary: Salary) {
        dataStore.edit { settings ->
            settings[PREFERENCES_POSITION_ID] = salary.positionId!!
            settings[PREFERENCES_USERNAME] = salary.username!!
        }
    }

    suspend fun savePassCode(passcode: String) {
        dataStore.edit { settings ->
            settings[PREFERENCES_PASS_CODE] = passcode
        }
    }

    suspend fun clearSettings() {
        dataStore.edit { settings ->
            settings.clear()
        }
    }

}