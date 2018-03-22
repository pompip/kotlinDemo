package com.example.chong.kotlindemo.util

import android.os.AsyncTask
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * Created by chong on 2018/3/19.
 */
object NetUtil {
    val client: OkHttpClient = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();

    fun request(url: String, call: (Response) -> Unit) {
        val task = NetTask(call)
        task.execute(url)

    }

    class NetTask(private val call: (Response) -> Unit) : AsyncTask<String, Int, Response>() {

        override fun doInBackground(vararg params: String?): Response {
            val r = Request.Builder().url(params[0]).get().build();

            val response = client.newCall(r).execute()
            if (response.isSuccessful) {
                return response
            } else {
                return Response.Builder().build();
            }
        }

        override fun onPostExecute(result: Response) {
            super.onPostExecute(result)
            call.invoke(result)
        }
    }


}




