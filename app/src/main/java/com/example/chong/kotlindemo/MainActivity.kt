package com.example.chong.kotlindemo

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.chong.kotlindemo.service.MyJobService
import com.example.chong.kotlindemo.service.MyKeepLiveService
import com.example.chong.kotlindemo.service.MyMessageService
import com.example.chong.kotlindemo.service.MyPushService
import com.example.chong.kotlindemo.util.loge
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startJobService() {
        val jobID = 111;
        val componentName = ComponentName(applicationContext, MyJobService::class.java);
        val builder = JobInfo.Builder(jobID, componentName)
        val jobInfo = builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .setPeriodic(3000)
                .setPersisted(true)
                .build()
        val tm = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        tm.schedule(jobInfo);
    }

    fun startKeepLiveService() {
        startService(Intent(applicationContext, MyKeepLiveService::class.java))
        startService(Intent(applicationContext, MyPushService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        startService(Intent(applicationContext, MyMessageService::class.java))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startJobService()
        }
        startKeepLiveService()
        initView()

    }

    fun initView() {
        button_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.first -> loge(1)
                R.id.second
                -> loge(2)
                R.id.third
                -> loge(3)
            }
            return@setOnNavigationItemSelectedListener true
        }
        button_navigation.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.first -> loge(1)
                R.id.second
                -> loge(2)
                R.id.third
                -> loge(3)
                else -> loge("empty")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                toChatActivity();
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun toChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }
}
