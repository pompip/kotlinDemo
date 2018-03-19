package com.example.chong.kotlindemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class ChatActivity : AppCompatActivity() {
    val baseUrl = "http://chongxxx.asuscomm.com:8083/websocket/hello"

    val socketListener = object :WebSocketListener(){
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            runOnUiThread {
            Toast.makeText(this@ChatActivity,"onOpen",Toast.LENGTH_SHORT).show();}
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
          runOnUiThread {
           textView.append(response.toString())
          }
        }

        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
            super.onClosing(webSocket, code, reason)
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            super.onMessage(webSocket, text)
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            super.onMessage(webSocket, bytes)
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            super.onClosed(webSocket, code, reason)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val okClient = initOkHttp();
        val request = Request.Builder().url(baseUrl).build()
        val newWebSocket = okClient?.newWebSocket(request, socketListener)



        sendButton.setOnClickListener { view ->

                val sendMessage = editText.text.toString()
                if (!TextUtils.isEmpty(sendMessage)) {
                    sendMessage(sendMessage)
                    newWebSocket?.send(sendMessage)
                }


            }
    }

    private fun sendMessage(message: String) {

    }

    fun initOkHttp(): OkHttpClient? {
        return OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();

    }
}
