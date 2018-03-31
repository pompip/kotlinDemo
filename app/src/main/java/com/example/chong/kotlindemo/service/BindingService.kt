package com.example.chong.kotlindemo.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class BindingService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return MyBinder();
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class MyBinder :Binder(){
        fun getService():BindingService{
            return this@BindingService
        }

    }
}
