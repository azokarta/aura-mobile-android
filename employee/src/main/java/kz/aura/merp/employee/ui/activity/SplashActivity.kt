
package kz.aura.merp.employee.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import kz.aura.merp.employee.util.LanguageHelper.updateLanguage
import kz.aura.merp.employee.databinding.ActivitySplashBinding
import kz.aura.merp.employee.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateLanguage(this)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

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