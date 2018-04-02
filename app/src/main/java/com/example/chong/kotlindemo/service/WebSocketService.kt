package com.example.chong.kotlindemo.service

import android.app.Service
import android.content.Intent
import android.os.*
import com.example.chong.kotlindemo.entity.MsgSystem
import com.example.chong.kotlindemo.entity.MsgUser
import com.example.chong.kotlindemo.util.NetUtil
import com.example.chong.kotlindemo.util.loge
import com.example.chong.kotlindemo.util.notifyMsg
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.jetbrains.annotations.Contract


class WebSocketService : Service() {
    private val baseUrl = NetUtil.BaseURL + "websocket"

    var gson = Gson();
    var jsonParser = JsonParser();
    lateinit var messenger: Messenger;
    var msgMessenger: Messenger? = null;
    lateinit var handler: Handler;
    var isWebSocketLive = false;

    override fun onCreate() {
        super.onCreate()
        messenger = Messenger(Handler({
            when (it.what) {
                100 -> msgMessenger = it.replyTo;
                200 -> {
                    val msg = handler.obtainMessage(3000);
                    msg.data = it.data;
                    handler.sendMessage(msg);
                }
            }
            return@Handler true;
        }))
        startWebSocket();

    }

    override fun onBind(intent: Intent): IBinder? {
        return messenger.binder;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!handler.looper.thread.isAlive){
            startWebSocket()
        }


        return Service.START_STICKY
    }

    private fun startWebSocket() {
        val thread = HandlerThread("webSocket")
        thread.start()
        handler = Handler(thread.looper, MyHandlerCallback())
        handler.sendEmptyMessage(1000)
        thread.quit()
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.sendEmptyMessage(2000)
    }


    private inner class MyHandlerCallback : Handler.Callback {
        lateinit var webSocket: WebSocket;
        override fun handleMessage(msg: Message?): Boolean {
            when (msg?.what) {

                1000 -> {
                    val request = Request.Builder().url(baseUrl).build()
                    webSocket = NetUtil.client.newWebSocket(request, MyWebSocketListener())
                }
                2000 -> {
                    webSocket.cancel()
                }
                3000 -> {
                    val data = msg.data;
                    data.classLoader = javaClass.classLoader
                    val msgUser = data.getParcelable<MsgUser>("msg")
                    val s = gson.toJson(msgUser)
                    val sendResult = webSocket.send(s)
                    val reply = Message();
                    reply.what = 30;
                    reply.arg1 = if (sendResult) 1 else 0;
                    msgMessenger?.send(reply)
                }
            }
            return true;
        }
    }


    private inner class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            webSocket?.send("from android")
            loge(this, "open ")
        }
        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            handler.looper.quit()
            loge(this, "onFailure " + t?.message + " webSocket isAlive:" + isWebSocketLive)

        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            handler.looper.quit()
            loge(this, reason.toString() + " webSocket isAlive:" + isWebSocketLive)
        }

        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            loge(this, text.toString())
            if (msgMessenger != null) {
                val parse = jsonParser.parse(text);
                val cmd = parse.asJsonObject.get("cmd").asInt

                if (cmd == 1) {
                    val msgUser = gson.fromJson<MsgUser>(text, MsgUser::class.java)
                    val message = Message();
                    message.what = 10;
                    val bundle = Bundle()
                    bundle.putParcelable("msg", msgUser)
                    message.data = bundle;
                    msgMessenger?.send(message);

                } else if (cmd == 2) {

                    val msgSystem = gson.fromJson<MsgSystem>(text, MsgSystem::class.java);
                    val message = Message();
                    message.what = 20;
                    val bundle = Bundle()
                    bundle.putParcelable("msg", msgSystem);
                    message.data = bundle;
                    msgMessenger?.send(message)
                }
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        }


    }
}
