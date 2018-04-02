package com.example.chong.kotlindemo.service

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import com.example.chong.kotlindemo.util.loge


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MyJobService : JobService() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        loge(this,"onStartCommand")

        return Service.START_NOT_STICKY
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    val handler = Handler(Handler.Callback {
        val param = it.obj as JobParameters
        jobFinished(param, true)
        startService(Intent(applicationContext, WebSocketService::class.java))
        return@Callback true
    })

    override fun onStartJob(params: JobParameters?): Boolean {
        handler.sendMessage(handler.obtainMessage(1,params))
        loge(this,"onStartJob")
        return true
    }


}
