package com.text.finalproject

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Food Delivery")
            .setContentText("Your food will be delivered soon")
            .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your notification icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

       // NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "food_delivery_channel" // Use your actual channel ID
        private const val NOTIFICATION_ID = 1

        fun scheduleNotification(context: Context, deliveryTimeInMillis: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java)

            // Specify FLAG_IMMUTABLE or FLAG_MUTABLE based on the Android version
            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flag)
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, deliveryTimeInMillis, pendingIntent)
        }
    }
}
