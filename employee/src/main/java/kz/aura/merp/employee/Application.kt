package kz.aura.merp.employee

import android.app.Application
import android.view.MotionEvent
import android.widget.EditText
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import kz.aura.merp.employee.util.Constants


@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(Constants.YANDEX_MAP_API_KEY)
    }
}