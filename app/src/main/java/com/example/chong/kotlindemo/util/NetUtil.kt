package com.example.chong.kotlindemo.util

import android.os.AsyncTask
import com.example.chong.kotlindemo.api.ApiServer
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by chong on 2018/3/19.
 */
object NetUtil {
    const val BaseURL = "http://chongxxx.asuscomm.com:8083/"
//    const val BaseURL = "http://i.lovexiangqing.top/"
    val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();
    val retrofit = Retrofit.Builder().client(client)
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    val apiServer = retrofit.create(ApiServer::class.java)

    fun request(url: String, call: (Response) -> Unit) {
        val task = NetTask(call)
        task.execute(url)

    }

    class NetTask(private val call: (Response) -> Unit) : AsyncTask<String, Int, Response>() {

        override fun doInBackground(vararg params: String): Response {
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

