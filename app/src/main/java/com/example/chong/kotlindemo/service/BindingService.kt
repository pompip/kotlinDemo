package com.example.chong.kotlindemo.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import com.example.chong.kotlindemo.dao.DaoUtil
import com.example.chong.kotlindemo.entity.MsgSystem
import com.example.chong.kotlindemo.entity.MsgUser
import com.example.chong.kotlindemo.util.loge
import com.example.chong.kotlindemo.util.notifyMsg
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue

class BindingService : Service(), ServiceConnection {
    var userMsgCallback: ((MsgUser) -> Unit)? = null;
    var toUserID: String? = null;
    var handlerCallback = HandlerCallback();


    fun getOnlineUserList(): List<Pair<String, Int>> {
        return handlerCallback.onlineUserList;
    }

    fun sendMsg(msg: String, toUserID: String) {
        val msgUser = MsgUser()
        msgUser.fromUserID = handlerCallback.meID!!;
        msgUser.toUserID = toUserID;
        msgUser.time = System.currentTimeMillis();
        msgUser.msgData = msg;
        msgUser.cmd = 1;

        val message = Message()
        message.what = 200;
        val b = Bundle();
        b.putParcelable("msg", msgUser)
        message.data = b

        messenger?.send(message)
    }

    fun registerUserMsgCallback(userID: String, callback: (MsgUser) -> Unit) {
        this.userMsgCallback = callback;
        toUserID = userID;

    }

    fun unRegisterUserMsgCallback() {

        userMsgCallback = null;
        toUserID = null;
    }

    var onlineCallback: ((List<Pair<String, Int>>) -> Unit)? = null;
    fun registerOnlineCallback(callback: (List<Pair<String, Int>>) -> Unit) {
        this.onlineCallback = callback;
    }

    fun unRegisterOnlineCallback() {
        this.onlineCallback = null;
    }

    override fun onBind(intent: Intent): IBinder {
        return MyBinder();
    }

    override fun onCreate() {
        super.onCreate()
        val intent = Intent(this, WebSocketService::class.java);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }

    var messenger: Messenger? = null;
    val handler = Handler(handlerCallback)

    override fun onServiceDisconnected(name: ComponentName?) {
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        val message = Message()
        message.what = 100;
        message.replyTo = Messenger(handler);
        messenger = Messenger(service);
        messenger?.send(message)
    }

    inner class MyBinder : Binder() {
        fun getService(): BindingService {
            return this@BindingService
        }
    }

    inner class HandlerCallback : Handler.Callback {
        var meID: String? = null;
        val onlineUserList = ArrayList<Pair<String, Int>>();
        override fun handleMessage(msg: Message?): Boolean {
            val data = msg?.data ?: return true
            data.classLoader = javaClass.classLoader
            when (msg.what) {
                10 -> {
                    val msgSystem = data.getParcelable<MsgUser>("msg")
                    DaoUtil.insert(this@BindingService, msgSystem)
                    if (userMsgCallback != null) {
                        userMsgCallback?.invoke(msgSystem)
                    } else {
                        notifyMsg(this@BindingService, "你有新的消息...", msgSystem.toUserID)
                    }
                }
                20 -> {
                    val msgSystem = data.getParcelable<MsgSystem>("msg")
                    meID = msgSystem.meID;
                    val onlineUser = msgSystem.onlineUser;
                    if (onlineCallback != null) {
                        onlineUserList.clear();
                        onlineUserList.add(Pair("all", 0))
                        onlineUser.forEach {
                            if (!meID.equals(it)) {
                                onlineUserList.add(Pair(it, 0))
                            }
                        }
                        onlineCallback?.invoke(onlineUserList)
                    } else {
                        notifyMsg(this@BindingService, "有朋友上线了...", null)
                    }
                }
                30 -> {
                    loge(this, if (msg.arg1 == 1) "msg发送成功" else "msg发送失败")
                }
            }
            return true;
        }

    }
}
