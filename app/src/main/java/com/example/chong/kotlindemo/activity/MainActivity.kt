package com.example.chong.kotlindemo.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import com.example.chong.kotlindemo.App
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.entity.MsgSystem
import com.example.chong.kotlindemo.fragment.ArticleListFragment
import com.example.chong.kotlindemo.fragment.MyCommFragment
import com.example.chong.kotlindemo.service.MyMessageService
import com.example.chong.kotlindemo.util.loge
import com.example.chong.kotlindemo.util.startJobService
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ServiceConnection {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startJobService(this);
        }
        initView()
        val intent = Intent(this, MyMessageService::class.java);
        intent.putExtra("from", 1);
        bindService(intent, this, Context.BIND_AUTO_CREATE);


    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val messenger = Messenger(service);
        val message = Message()
        message.what = 100;
        message.replyTo = Messenger(Handler({

            val data = it.data
            data.classLoader = javaClass.classLoader
            val msgSystem =  data.getParcelable<MsgSystem>("msg")

            MyCommFragment.instances.newComm(msgSystem.onlineUser);
            return@Handler true;
        }));
        messenger.send(message)
    }

    fun toFragment(i: Int) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        if (i == 1) {
            if (ArticleListFragment.instances.isAdded) {
                beginTransaction.show(ArticleListFragment.instances)
            } else {
                beginTransaction.add(R.id.container, ArticleListFragment.instances, "article");
            }
            if (MyCommFragment.instances.isAdded) {
                beginTransaction.hide(MyCommFragment.instances)
            }
        } else if (i == 2) {
            if (MyCommFragment.instances.isAdded) {
                beginTransaction.show(MyCommFragment.instances)
            } else {
                beginTransaction.add(R.id.container, MyCommFragment.instances, "mycomm");
            }
            if (ArticleListFragment.instances.isAdded) {
                beginTransaction.hide(ArticleListFragment.instances)
            }
        }
        beginTransaction.commit();
    }

    fun initView() {
        button_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.first -> toFragment(1);
                R.id.second
                -> toFragment(2);
                R.id.third
                -> loge(3)
            }
            return@setOnNavigationItemSelectedListener true
        }
        button_navigation.selectedItemId = R.id.first;
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                App.instances.toNight()
                recreate()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
