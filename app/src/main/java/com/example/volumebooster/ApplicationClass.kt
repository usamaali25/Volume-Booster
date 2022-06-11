package com.example.volumebooster

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

class ApplicationClass: Application() {
    companion object{
        val CHANNEL_ID_1 = "channel1"
        val CHANNEL_ID_2 = "channel2"
        val ACTION_PREVIOUS = "actionprevious"
        val ACTION_NEXT= "actionnext"
        val ACTION_PLAY = "actionplay"

    }
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel1 = NotificationChannel(
                CHANNEL_ID_1,"channel(1)",
                NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "channel 1 Desc..."

            val channel2 = NotificationChannel(
                CHANNEL_ID_2,"channel(2)",
                NotificationManager.IMPORTANCE_HIGH)
            channel2.description = "channel 2 Desc..."

            val notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
            notificationManager.createNotificationChannel(channel2)
        }
    }
}