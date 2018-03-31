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
import java.util.concurrent.LinkedBlockingDeque


class MyMessageService : Service() {
    private val baseUrl = "http://chongxxx.asuscomm.com:8083/websocket"
    private val thread = HandlerThread("webSocket")
    lateinit var webSocket: WebSocket;
    lateinit var handler: Handler;
    var gson = Gson();
    var jsonParser = JsonParser();
    lateinit var messenger: Messenger;
    var msgMessenger: Messenger? = null;
    var onlineMessage: Messenger? = null;


    override fun onBind(intent: Intent): IBinder? {
        timer.start();
        return messenger.binder;
    }

    override fun onUnbind(intent: Intent?): Boolean {
        timer.cancel()
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        thread.start()
        handler = Handler(thread.looper, MyHandlerCallback())
        messenger = Messenger(handler)
        handler.post {
            val request = Request.Builder().url(baseUrl).build()
            webSocket = NetUtil.client.newWebSocket(request, MyWebSocketListener())
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        webSocket.cancel()
        thread.quit()

    }

    fun sendMessage(msg: String): Boolean {
        return webSocket.send(msg)
    }

    private inner class MyHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message?): Boolean {
            when (msg?.what) {
                100 -> onlineMessage = msg.replyTo;
                200 -> msgMessenger = msg.replyTo;
            }
            return true;
        }

    }

    fun addMsg(msg: MsgUser) {
        queue.offer(msg)
        if (msgMessenger == null) {
            notifyMsg(this@MyMessageService, "你有新的消息了！！")
        }
    }

    var queue = LinkedBlockingDeque<MsgUser>()
    var timer = object :CountDownTimer(Long.MAX_VALUE,1000){
        override fun onFinish() {

        }

        override fun onTick(millisUntilFinished: Long) {
            if (msgMessenger != null) {
                val poll = queue.poll() ?: return
                loge(this, poll)
                val message = handler.obtainMessage();
                val bundle = Bundle()
                bundle.putParcelable("msg", poll)
                message.data = bundle;
                msgMessenger?.send(message);
            }
        }

    }


    private inner class MyWebSocketListener : WebSocketListener() {
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
            loge("oncloseing  " + reason.toString())
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            val parse = jsonParser.parse(text);
            val cmd = parse.asJsonObject.get("cmd").asInt
            if (cmd == 1) {
                val msgUser = gson.fromJson<MsgUser>(text, MsgUser::class.java)
                addMsg(msgUser);
            } else if (cmd == 2) {
                if (onlineMessage != null) {
                    val msgSystem = gson.fromJson<MsgSystem>(text, MsgSystem::class.java);
                    val message = Message();
                    val bundle = Bundle()
                    bundle.putParcelable("msg", msgSystem);
                    message.data = bundle;
                    onlineMessage?.send(message)
                } else {
                    notifyMsg(this@MyMessageService, "在线联系人列表更新了！！")
                }
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            loge(reason.toString())
        }
    }
}
