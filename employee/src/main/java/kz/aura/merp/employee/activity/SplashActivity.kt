package kz.aura.merp.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.Staff
import kz.aura.merp.employee.util.Helpers.clearPreviousAndOpenActivity
import kz.aura.merp.employee.util.Helpers.getToken
import kz.aura.merp.employee.util.Helpers.openActivityByPositionId
import kz.aura.merp.employee.util.LanguageHelper.updateLanguage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateLanguage(this)
        setContentView(R.layout.activity_splash)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        val json = Gson().toJson(Staff(1028, "Sultan", 1028, "32323","awdawda",
            arrayListOf(), null
            ))
        editor.putString("data", json)
        editor.apply()

        logo.alpha = 0f
        logo.animate().setDuration(1500).alpha(1f).withEndAction {
            if (getToken(this) != "") {
                openActivityByPositionId(this)
            } else {
                clearPreviousAndOpenActivity(this, AuthorizationActivity())
            }
        }
    }

}
