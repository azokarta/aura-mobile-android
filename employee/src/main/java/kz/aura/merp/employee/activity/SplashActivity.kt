package kz.aura.merp.employee.activity

import android.content.Intent
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
import kz.aura.merp.employee.util.PassCodeStatus

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateLanguage(this)
        setContentView(R.layout.activity_splash)

        logo.alpha = 0f
        logo.animate().setDuration(1500).alpha(1f).withEndAction {
          /*  val intent = Intent(this, PassCodeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("passCodeStatus", PassCodeStatus.CREATE)
            startActivity(intent)*/
         if (!getToken(this).isNullOrBlank()) {
                val intent = Intent(this, PassCodeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("passCodeStatus", PassCodeStatus.VERIFY)
                startActivity(intent)
            } else {
                clearPreviousAndOpenActivity(this, AuthorizationActivity())
            }
        }
    }

}