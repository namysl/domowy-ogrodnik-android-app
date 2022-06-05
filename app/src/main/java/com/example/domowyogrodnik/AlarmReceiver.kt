package com.example.domowyogrodnik

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent){
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val plantPhoto = intent.getStringExtra("plantPhoto")
        val plantName = intent.getStringExtra("plantName")
        val plantChore = intent.getStringExtra("plantChore")

        val alarmNotificationBuilder = AlarmNotificationBuilder(context, plantPhoto, plantName, plantChore)
        val notification = alarmNotificationBuilder.getNotificationBuilder().build()
        alarmNotificationBuilder.getManager().notify(150, notification)
    }
}
