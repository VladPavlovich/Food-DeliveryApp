package com.text.finalproject


import android.app.Service
import android.content.Intent
import android.os.IBinder

class NotificationService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val deliveryTimeInMillis = intent?.getLongExtra("deliveryTimeInMillis", 0L) ?: 0L
        NotificationReceiver.scheduleNotification(this, deliveryTimeInMillis)
        return START_NOT_STICKY
    }
}
