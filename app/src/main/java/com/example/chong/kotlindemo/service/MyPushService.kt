package com.example.chong.kotlindemo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyPushService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }
}
