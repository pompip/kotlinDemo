package com.example.chong.kotlindemo.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Messenger

class MyKeepLiveService : Service() {


    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        val handler = Handler({
            return@Handler false
        });
        var messenger = Messenger(handler)
        super.onCreate()
    }
}
