package kz.aura.merp.customer.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.aura.R
import kz.aura.merp.customer.utils.Helpers.loadLocale
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Animate our logotype
        // When the animation is over, we are going to go to another screen
        auralogo.alpha = 0f
        auralogo.animate().setDuration(1500).alpha(1f).withEndAction {
            loadLocale(this)
            val intent = Intent(applicationContext, AuthorizationActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}