package kz.aura.merp.employee.base

import android.content.SharedPreferences
import com.google.gson.Gson
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.util.CountryCode
import kz.aura.merp.employee.util.Language

class AppPreferences(private val preferences: SharedPreferences) {

    companion object {
        private const val TOKEN = "TOKEN"
        private const val LANGUAGE = "LANGUAGE"
        private const val PASSCODE = "PASSCODE"
        private const val COUNTRY_CALLING_CODE = "COUNTRY_CALLING_CODE"
        private const val SALARY = "SALARY"
    }

    var token: String?
        get() = preferences.getString(TOKEN, null)
        set(value) = preferences.edit().putString(TOKEN, value).apply()

    var language: String
        get() = preferences.getString(LANGUAGE, Language.RU.value)!!
        set(value) = preferences.edit().putString(LANGUAGE, value).apply()

    var passcode: String?
        get() = preferences.getString(PASSCODE, null)
        set(value) = preferences.edit().putString(PASSCODE, value).apply()

    var countryCallingCode: String?
        get() = preferences.getString(COUNTRY_CALLING_CODE, null)
        set(value) = preferences.edit().putString(COUNTRY_CALLING_CODE, value).apply()

    var salary: Salary?
        get() = preferences.getString(SALARY, null)?.let { value ->
            Gson().fromJson(value, Salary::class.java)
        }
        set(value) = preferences.edit().putString(SALARY, Gson().toJson(value)).apply()

    fun clear() = preferences.edit().clear().apply()

    fun remove(key: String) = preferences.edit().remove(key).apply()

    fun getCountryCode() = CountryCode.values().find { it.phoneCode == countryCallingCode } ?: CountryCode.KZ
}