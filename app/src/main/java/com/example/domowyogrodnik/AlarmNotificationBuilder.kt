package com.example.domowyogrodnik

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.app.NotificationCompat


class  AlarmNotificationBuilder(base: Context, private var plantPhoto: String?,
                                private var plantName: String?, private var plantChore: String?): ContextWrapper(base){
    private val channelID = "PlantsID"
    private val channelName = "PlantsAppAlert"

    private var manager: NotificationManager? = null

    init{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannels()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannels(){
        // create a channel for Android 26+
        val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(true)

        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager{
        // get the manager
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager as NotificationManager
    }

    fun getNotificationBuilder(): NotificationCompat.Builder{
        val intent = Intent(this, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val photoBitmap = BitmapFactory.decodeFile("$plantPhoto/profile.jpg")
        return NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle("Czas na <strong>${plantChore}</strong>!".toSpanned())
            .setContentText("Twoja roślina <strong>${plantName}</strong> wymaga uwagi.".toSpanned())
            .setSmallIcon(R.drawable.ic_plants)
            .setLargeIcon(photoBitmap)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(photoBitmap).bigLargeIcon(null))
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
    }

    private fun String.toSpanned(): Spanned{
        // helper function to format a text with markup
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        }
        else {
            @Suppress("DEPRECATION")
            return Html.fromHtml(this)
        }
    }
}