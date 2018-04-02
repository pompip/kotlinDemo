package com.example.chong.kotlindemo.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.activity.ChatActivity

var notificationID: Int = 1;
val channelID = "com.chong.channel.1" // The user-visible name of the channel.


@RequiresApi(Build.VERSION_CODES.O)
private fun createChannel(manager: NotificationManager) {
    val channelName = "my_package_channel"
    val description = "unReadMessage" // The user-visible description of the channel.
    val importance = NotificationManager.IMPORTANCE_HIGH
    var mChannel = manager.getNotificationChannel(channelID)
    if (mChannel == null) {
        mChannel = NotificationChannel(channelID, channelName, importance)
        mChannel.setDescription(description)
        mChannel.enableVibration(true)
        mChannel.setVibrationPattern(longArrayOf(50))
        manager.createNotificationChannel(mChannel)
    }
}

fun notifyMsg(context: Context, msg: String, toUserID: String?) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createChannel(manager)
    }

    val build = NotificationCompat.Builder(context, channelID)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentText(msg)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setChannelId(channelID)
            .setContentTitle("未读消息")
            .setShowWhen(false)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setVibrate(longArrayOf(10))

    if (toUserID != null) {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra("userID", toUserID);
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context,
                1,
                intent,
                PendingIntent.FLAG_ONE_SHOT)
        build.setContentIntent(pendingIntent)
    }

    val notification = build.build()
    manager.notify(notificationID++, notification)

}