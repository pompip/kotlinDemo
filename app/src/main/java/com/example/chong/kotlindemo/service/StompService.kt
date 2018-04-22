package com.example.chong.kotlindemo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
//import android.util.Log
//import ua.naiksoftware.stomp.Stomp


class StompService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val TAG  = "StompService";



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        val mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/example-endpoint/websocket")
//        mStompClient.connect()
//
//        mStompClient.topic("/topic/greetings").subscribe({ topicMessage -> Log.d(TAG, topicMessage.getPayload()) })
//
//        mStompClient.send("/topic/hello-msg-mapping", "My first STOMP message!").subscribe()
        return super.onStartCommand(intent, flags, startId)
    }
}
