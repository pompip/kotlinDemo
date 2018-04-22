package com.example.chong.kotlindemo.service

import android.app.Service
import android.content.Intent
import android.os.*
import com.example.chong.kotlindemo.dao.DaoUtil
import com.example.chong.kotlindemo.entity.ChatUserInfo
import com.example.chong.kotlindemo.entity.MsgCmd
import com.example.chong.kotlindemo.entity.MsgEntity
import com.example.chong.kotlindemo.util.NetUtil
import com.example.chong.kotlindemo.util.loge
import com.example.chong.kotlindemo.util.notifyMsg
import com.google.gson.Gson
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class BindingService : Service() {
    private val baseUrl = NetUtil.BaseURL + "webSocket/chat"
    var userMsgCallback: ((MsgEntity) -> Unit)? = null;
    var meInfo: ChatUserInfo? = null;
    val gson = Gson()

    fun sendMsg(msg: String, toUserID: String) {
        val msgUser = MsgEntity()
        msgUser.toUserID = toUserID;
        msgUser.time = System.currentTimeMillis();
        msgUser.msgData = msg;
        val message = handler.obtainMessage();
        message.what = 2000;
        message.obj = msgUser;
        handler.sendMessage(message)

    }

    fun registerUserMsgCallback(callback: (MsgEntity) -> Unit) {
        this.userMsgCallback = callback;

    }

    fun unRegisterUserMsgCallback() {
        userMsgCallback = null;
    }

    override fun onBind(intent: Intent): IBinder {
        return MyBinder();
    }

    lateinit var handler: Handler;
    override fun onCreate() {
        super.onCreate()
        loge(this, "onCreate")
        val thread = HandlerThread("webSocket");
        thread.start()
        handler = Handler(thread.looper, MyHandlerCallback())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        loge(this, "onStartCommand")
        handler.sendMessage(handler.obtainMessage(1000))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        loge(this, "onDestroy")
    }


    inner class MyBinder : Binder() {
        fun getService(): BindingService {
            return this@BindingService
        }
    }


    private inner class MyHandlerCallback : Handler.Callback {
        var webSocket: WebSocket? = null;
        override fun handleMessage(msg: Message?): Boolean {
            when (msg?.what) {
                1000 -> {
                    val request = Request.Builder().url(baseUrl).build()
                    if (webSocket == null) {
                        webSocket = NetUtil.client.newWebSocket(request, MyWebSocketListener())
                    } else {
                        if (!webSocket?.send(createMsg(2, "android"))!!) {
                            webSocket = NetUtil.client.newWebSocket(request, MyWebSocketListener())
                        }
                    }
                }
                2000 -> {
                    val s = gson.toJson(msg.obj)
                    webSocket?.send(createMsg(1, s))

                }
            }
            return true;
        }
    }

    fun createMsg(cmd: Int, body: String): String {
        val msgCmd = MsgCmd(cmd, body);
        return gson.toJson(msgCmd)
    }


    inner class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            webSocket?.send(createMsg(2, "android"))
            loge(this, "open ")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            loge(this, "onFailure")
            t?.printStackTrace()
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            loge(this, "onClosed    code:" + code + "  reason:  " + reason)
        }

        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
            loge(this, "onClosing  code:" + code + "  reason:  " + reason)
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            loge(this, text.toString())
            val msgCmd = gson.fromJson(text, MsgCmd::class.java)
            when (msgCmd.cmd) {
                1 -> {
                    val msgEntity = gson.fromJson<MsgEntity>(msgCmd.body, MsgEntity::class.java)

                    DaoUtil.insert(this@BindingService, msgEntity)
                    if (userMsgCallback != null) {
                        userMsgCallback?.invoke(msgEntity)
                    } else {
                        notifyMsg(this@BindingService, msgEntity.msgData, msgEntity.toUserID);
                    }
                }
                2 -> {
                    meInfo = gson.fromJson(msgCmd.body, ChatUserInfo::class.java);
                }
                3 -> {

                }
            }


        }
    }
}
