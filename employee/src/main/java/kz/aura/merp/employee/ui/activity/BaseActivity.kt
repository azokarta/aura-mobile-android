package kz.aura.merp.employee.ui.activity

import android.R
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kz.aura.merp.employee.util.getLanguage
import kz.aura.merp.employee.util.updateLocale


open class BaseActivity: AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // get chosen language from shared preference
        val localeToSwitchTo = getLanguage(newBase)
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