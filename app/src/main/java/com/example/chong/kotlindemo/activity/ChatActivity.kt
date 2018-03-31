package com.example.chong.kotlindemo.activity

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.entity.MsgUser
import com.example.chong.kotlindemo.service.MyMessageService
import com.example.chong.kotlindemo.util.loge
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.item_view.view.*

class ChatActivity : AppCompatActivity(), ServiceConnection {
    var msgList = ArrayList<MsgUser>()
    val msgMessenger = Messenger(Handler({

        val data = it.data
        data.classLoader = javaClass.classLoader;
        val parcelable = data.getParcelable<MsgUser>("msg")
        msgList.add(parcelable)
        loge(this,parcelable)

        chat_recycler_view.adapter.notifyDataSetChanged()
        return@Handler true
    }))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val userID = intent.getStringExtra("userID");
        title = "与 " + userID + "  交流中..."

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        notificationManager.cancelAll()
        initView()

        val intent = Intent(this, MyMessageService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        send_button.setOnClickListener { view ->
            val sendMessage = chat_edit_text.text.toString()
            if (!TextUtils.isEmpty(sendMessage)) {
                chat_edit_text.setText("", TextView.BufferType.EDITABLE);
            }
        }
    }

    fun initView() {
        chat_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                    return object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false)) {};
                }

                override fun getItemCount(): Int {
                    return msgList.size
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                    val msgUser = msgList[position]
                    holder?.itemView?.text_title?.text = msgUser.fromUserID
                    holder?.itemView?.text_content?.text = msgUser.msgData
                }
            }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val messenger = Messenger(service)
        val msg = Message()
        msg.what = 200;
        msg.replyTo = msgMessenger;
        messenger.send(msg)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }


}
