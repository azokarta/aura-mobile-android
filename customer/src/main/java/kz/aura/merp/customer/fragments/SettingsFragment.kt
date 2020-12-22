package kz.aura.merp.customer.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.aura.R
import kz.aura.merp.customer.activities.MainActivity
import kz.aura.merp.customer.utils.Helpers.setLocale


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        (activity as MainActivity).supportActionBar?.title = getString(R.string.settings)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if (p1.equals("list_language_preference")) {
            setLocale(p0?.getString(p1, "").toString(), this.requireContext())
            val connectionPref: Preference? = p1?.let { findPreference(it) }
            connectionPref?.summary = p0!!.getString(p1, "")
            (activity as MainActivity).recreate()
        }
    }
}