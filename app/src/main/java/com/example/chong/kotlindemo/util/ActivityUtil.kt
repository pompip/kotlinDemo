package com.example.chong.kotlindemo.util

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import com.example.chong.kotlindemo.App
import com.example.chong.kotlindemo.activity.ChatActivity
import com.example.chong.kotlindemo.activity.MarkdownEditActivity
import com.example.chong.kotlindemo.activity.MarkdownWebActivity
import com.example.chong.kotlindemo.entity.MyArticle
import com.example.chong.kotlindemo.service.MyJobService


fun toMarkdownWebActivity(context: Context,myArticle: MyArticle){
    val intent = Intent();
    intent.setClass(context,MarkdownWebActivity::class.java);
    intent.putExtra("content",myArticle)
    context.startActivity(intent);
}
fun toMarkdownEditActivity(context: Context,myArticle: MyArticle){
    val intent = Intent();
    intent.setClass(context,MarkdownEditActivity::class.java);
    intent.putExtra("content",myArticle)
    context.startActivity(intent);
}

fun toChatActivity(context: Context,toChatUserId:String){
    val intent = Intent(context,ChatActivity::class.java)
    intent.putExtra("userID",toChatUserId);
    context.startActivity(intent);
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun startJobService(context: Context) {
    val jobID = 111;
    val componentName = ComponentName(context, MyJobService::class.java);
    val builder = JobInfo.Builder(jobID, componentName)
    val jobInfo = builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
            .setPersisted(true)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .setPeriodic(3000)
            .setPersisted(true)
            .build()
    val tm = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    tm.schedule(jobInfo);
}