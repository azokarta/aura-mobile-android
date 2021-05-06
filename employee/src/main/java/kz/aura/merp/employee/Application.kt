package kz.aura.merp.employee

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import kz.aura.merp.employee.data.DataStoreRepository
import kz.aura.merp.employee.util.Constants
import javax.inject.Inject

@HiltAndroidApp
class Application @Inject constructor(
    val dataStoreRepository: DataStoreRepository
) : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(Constants.YANDEX_MAP_API_KEY)
    }
}