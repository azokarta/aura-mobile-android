
package kz.aura.merp.employee.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.util.LanguageHelper.updateLanguage
import kz.aura.merp.employee.databinding.ActivitySplashBinding
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.AuthViewModel

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val mAuthViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateLanguage(this)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        mAuthViewModel.token.observe(this, { token ->
            binding.logo.alpha = 0f
            binding.logo.animate().setDuration(1500).alpha(1f).withEndAction {
                if (token.isNotBlank()) {
                    val intent = Intent(this, PassCodeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("passCodeStatus", PassCodeStatus.VERIFY)
                    startActivity(intent)
                } else {
                    clearPreviousAndOpenActivity(this, AuthorizationActivity())
                }
            }
        })

        mAuthViewModel.getToken()
    }

}