package kz.aura.merp.employee

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import kz.aura.merp.employee.util.Constants

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(Constants.YANDEX_MAP_API_KEY)
    }
}