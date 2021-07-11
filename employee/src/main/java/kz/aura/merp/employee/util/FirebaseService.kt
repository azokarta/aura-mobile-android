package kz.aura.merp.employee.util

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kz.aura.merp.employee.Application.Companion.CHANNEL_DEFAULT
import kz.aura.merp.employee.R

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        Log.d("FirebaseService", "Received data: ${p0.data}")

        val notificationManager = NotificationManagerCompat.from(this)

        val notification = NotificationCompat.Builder(this, CHANNEL_DEFAULT)
            .setSmallIcon(R.drawable.we_logo_2024px)
            .setContentTitle(p0.data["title"])
            .setContentText(p0.data["body"])
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(4505, notification)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    private fun openActivity(activity: Class<*>) {
        val intent = Intent(this, activity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
    }
}