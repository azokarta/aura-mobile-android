package kz.aura.merp.employee.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivitySettingsBinding
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.util.LanguageHelper.getLanguage

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        binding.signOut.setOnClickListener {
            signOut()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val connectionPref: Preference? = findPreference("language")
            connectionPref!!.summary = getLanguage(this.requireContext())
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
            if (p1.equals("language")) {
                val lang = p0?.getString(p1, "").toString()
                LanguageHelper.updateLanguage(this.requireContext(), lang)
//                val position = definePosition(arrayListOf(mAuthViewModel.getSalary()!!))!!
//                openActivityByPosition(this, position)
            }
        }

    }

    private fun signOut() {
        PreferenceManager.getDefaultSharedPreferences(application)
            .edit()
            .remove("token")
            .remove("staff")
            .remove("staffId")
            .remove("phoneNumber")
            .apply()
        clearPreviousAndOpenActivity(this, AuthorizationActivity())
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}