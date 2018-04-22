package com.example.chong.kotlindemo.activity

import android.content.Intent
import android.os.*
import android.support.v7.app.AppCompatActivity
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.fragment.ArticleListFragment
import com.example.chong.kotlindemo.fragment.MyCommFragment
import com.example.chong.kotlindemo.service.BindingService
import com.example.chong.kotlindemo.util.startJobService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startJobService(this);
        }
        initView()
        startService(Intent(this,BindingService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
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
                -> {
                    val intent = Intent(this,LocalVideoPlayActivity::class.java)
                    startActivity(intent)
                }
                R.id.forth
                        ->{
                    val intent = Intent(this,VideoRecorderActivity::class.java)
                    startActivity(intent)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        button_navigation.selectedItemId = R.id.first;
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_settings -> {
//                App.instances.toNight()
//                recreate()
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}
