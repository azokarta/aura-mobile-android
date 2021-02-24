
package kz.aura.merp.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kz.aura.merp.employee.util.Helpers.clearPreviousAndOpenActivity
import kz.aura.merp.employee.util.Helpers.getToken
import kz.aura.merp.employee.util.LanguageHelper.updateLanguage
import kz.aura.merp.employee.databinding.ActivitySplashBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.PassCodeStatus

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateLanguage(this)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logo.alpha = 0f
        binding.logo.animate().setDuration(1500).alpha(1f).withEndAction {
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