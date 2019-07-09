package com.ned.optimaltime.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ned.optimaltime.R
import com.ned.optimaltime.util.TimerUtil

class TimerExpiredReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        TimerUtil.setAlarmSetTime(0, context)
        TimerUtil.setSecondsRemaining(0,context)
        sendNotification(context)
    }

    private fun sendNotification(context: Context) {
        val title = "Time's up!"
        val body = "Work hard \uD83D\uDE24 Play hard \uD83D\uDE0E"

        val notif: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "some_channel_id"
            val channelName = "Some Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channelDesc = "Notification when timer finishes in background"

            val notify : NotificationChannel = NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH)
            notify.description = channelDesc
            notif.createNotificationChannel(notify)

            val mBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(body)// message for notification
                .setAutoCancel(true) // clear notification after click
            val intent = Intent(context, TimerFragment::class.java)
            val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            mBuilder.setContentIntent(pi)
            notif.notify(0, mBuilder.build())

        }else{
            val notify :Notification = Notification.Builder(context)
            .setContentTitle(title)
                .setContentText(body)
//            .setSmallIcon(R.drawable.new_mail)
//            .setLargeIcon(aBitmap)
                .build()
            notify.flags = Notification.FLAG_AUTO_CANCEL

            notif.notify(0, notify)
        }
    }
}
