package kz.aura.merp.employee.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.preference.PreferenceManager
import java.util.*


object LanguageHelper {

    /**
     * Update the app language
     *
     * @param language Language to switch to.
     */
    fun updateLanguage(context: Context, language: String? = getLanguage(context)) {
        storeLanguage(context, language)
        val locale = Locale(language!!)
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val cfg = Configuration(res.configuration)
        cfg.locale = locale
        res.updateConfiguration(cfg, res.displayMetrics)
    }

    /**
     * Store the language selected by the user.
     * /!\ SHOULD BE CALLED WHEN THE USER CHOOSE THE LANGUAGE
     */
    fun storeLanguage(context: Context, language: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString("language", language)
            .apply()
    }

    /**
     * @return The stored user language or null if not found.
     */
    fun getLanguage(context: Context): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        val language = pref.getString("language", "ru")
        return if (!language.isNullOrEmpty()) {
            language
        } else {
            editor.putString("language", "ru")
            editor.apply()
            "ru"
        }
    }
}