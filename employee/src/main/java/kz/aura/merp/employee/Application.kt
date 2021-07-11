package kz.aura.merp.employee

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import kz.aura.merp.employee.util.Constants

@HiltAndroidApp
class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(Constants.YANDEX_MAP_API_KEY)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "getString(R.string.channel_name)"
            val descriptionText = "getString(R.string.channel_description)"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel1 = NotificationChannel(CHANNEL_DEFAULT, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = descriptionText
            }
            val channel2 = NotificationChannel(CHANNEL_IN_APP, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
            notificationManager.createNotificationChannel(channel2)
        }
    }

    companion object {
        const val CHANNEL_IN_APP = "in-app"
        const val CHANNEL_DEFAULT = "default"
    }
}