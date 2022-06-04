package com.example.domowyogrodnik

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val x = intent.getStringExtra("plant_photo")
        val y = intent.getStringExtra("plant_name")
        val z = intent.getStringExtra("chore")
        println("elo tu jest x: " + x)
        val notificationUtils = NotificationUtils(context, x, y, z)
        val notification = notificationUtils.getNotificationBuilder().build()
        notificationUtils.getManager().notify(150, notification)
    }
}
