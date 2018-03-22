package com.example.chong.kotlindemo.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.example.chong.kotlindemo.ChatActivity
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.util.NetUtil
import com.example.chong.kotlindemo.util.loge
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString


class MyMessageService : Service() {
    val baseUrl = "http://chongxxx.asuscomm.com:8083/websocket"
    lateinit var webSocket: WebSocket;
    var isBinded: Boolean = false;
    val binder: MyMessageBinder = MyMessageBinder();
    val socketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            sendMessage("from android")
            loge("open ")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            t?.printStackTrace()
            loge("onFailure " + t?.message)
            loge("onFailure " + response?.body()?.string())

        }

        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
            loge(reason.toString())
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            if (isBinded) {
                binder.callBack(text!!)
            } else {
                notifyMsg(text!!)
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            loge(reason.toString())
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        isBinded = true;
        return binder
    }

    override fun onRebind(intent: Intent?) {
        isBinded = true;
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBinded = false;
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        val request = Request.Builder().url(baseUrl).build()
        webSocket = NetUtil.client.newWebSocket(request, socketListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        loge("onStartComman")
        return super.onStartCommand(intent, flags, startId)
    }

    fun sendMessage(msg: String): Boolean {
        return webSocket.send(msg)
    }

    var notificationID: Int = 1;
    val channelID = "com.chong.channel.1" // The user-visible name of the channel.

    fun notifyMsg(msg: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(manager)
        }
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(applicationContext,
                1,
                intent,
                PendingIntent.FLAG_ONE_SHOT)

        val notification = NotificationCompat.Builder(applicationContext, channelID)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentText(msg)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setChannelId(channelID)
                .setContentIntent(pendingIntent)
                .setContentTitle("未读消息")
                .setShowWhen(false)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setVibrate(longArrayOf(10))
                .build()
        manager.notify(notificationID++, notification)

    }

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

    inner class MyMessageBinder : Binder() {
        lateinit var callBack: (String) -> Unit;
        fun sendMsg(msg: String) {
            sendMessage(msg)
        }
    }
}
