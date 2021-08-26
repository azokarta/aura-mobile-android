@file:Suppress("unused")

package kz.aura.merp.employee.initializer

import android.content.Context
import androidx.startup.Initializer
import kz.aura.merp.employee.BuildConfig
import timber.log.Timber

class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}