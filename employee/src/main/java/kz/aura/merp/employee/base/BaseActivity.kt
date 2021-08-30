package kz.aura.merp.employee.base

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.util.updateLocale
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity: AppCompatActivity() {

    @Inject
    lateinit var preferences: AppPreferences

    override fun attachBaseContext(newBase: Context) {
        // get chosen language from shared preference
        val localeToSwitchTo = "preferences.language"
        val localeUpdatedContext: ContextWrapper = updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

}