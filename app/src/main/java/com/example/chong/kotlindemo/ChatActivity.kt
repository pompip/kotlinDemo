package com.example.chong.kotlindemo

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.example.chong.kotlindemo.service.MyMessageService
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.item_view.*

class ChatActivity : AppCompatActivity() {
    lateinit var conn: ServiceConnection;
    lateinit var binder: MyMessageService.MyMessageBinder;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        notificationManager.cancelAll()

        conn = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                binder = service as MyMessageService.MyMessageBinder;
                binder.callBack = {
                    textView.text = it;
                }
            }

        }
        bindService(Intent(this, MyMessageService::class.java), conn, Context.BIND_AUTO_CREATE)
        sendButton.setOnClickListener { view ->
            val sendMessage = editText.text.toString()
            if (!TextUtils.isEmpty(sendMessage) && binder.isBinderAlive) {
                binder.sendMsg(sendMessage)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(conn)
    }


}
