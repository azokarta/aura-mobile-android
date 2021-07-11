package kz.aura.merp.employee.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_BRANCH
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_COMPANY
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_COUNTRY
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_COUNTRY_CALLING_CODE
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_DEPARTMENT
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_PASS_CODE
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_PHONE
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_POSITION
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_POSITION_ID
import kz.aura.merp.employee.data.DataStoreRepository.PreferenceKeys.PREFERENCES_STAFF_ID
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
        val PREFERENCES_PHONE = stringPreferencesKey("phone")
        val PREFERENCES_TOKEN = stringPreferencesKey("token")
        val PREFERENCES_POSITION_ID = longPreferencesKey("positionId")
        val PREFERENCES_USERNAME = stringPreferencesKey("username")
        val PREFERENCES_STAFF_ID = longPreferencesKey("staffId")
        val PREFERENCES_COMPANY = stringPreferencesKey("company")
        val PREFERENCES_COUNTRY = stringPreferencesKey("country")
        val PREFERENCES_BRANCH = stringPreferencesKey("branch")
        val PREFERENCES_POSITION = stringPreferencesKey("position")
        val PREFERENCES_DEPARTMENT = stringPreferencesKey("department")

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
            val phone = preferences[PREFERENCES_PHONE] ?: ""
            val staffId = preferences[PREFERENCES_STAFF_ID]
            val company = preferences[PREFERENCES_COMPANY]
            val country = preferences[PREFERENCES_COUNTRY]
            val branch = preferences[PREFERENCES_BRANCH]
            val position = preferences[PREFERENCES_POSITION]
            val department = preferences[PREFERENCES_DEPARTMENT]

            if (positionId != 0L && username.isNotBlank()) {
                Salary(positionId = positionId, username = username, phoneNumber = phone,
                    staffId = staffId, companyName = company, countryName = country, branchName = branch,
                    positionName = position, departmentName = department)
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
            settings[PREFERENCES_USERNAME] = salary.username ?: ""
            settings[PREFERENCES_PHONE] = salary.phoneNumber ?: ""
            settings[PREFERENCES_STAFF_ID] = salary.staffId ?: 0L
            settings[PREFERENCES_COMPANY] = salary.companyName ?: ""
            settings[PREFERENCES_COUNTRY] = salary.countryName ?: ""
            settings[PREFERENCES_BRANCH] = salary.branchName ?: ""
            settings[PREFERENCES_POSITION] = salary.positionName ?: ""
            settings[PREFERENCES_DEPARTMENT] = salary.departmentName ?: ""
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