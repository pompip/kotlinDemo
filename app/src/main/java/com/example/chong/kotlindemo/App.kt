package com.example.chong.kotlindemo

import android.app.Application
import android.content.Context
import android.provider.CalendarContract
import android.support.v7.app.AppCompatDelegate

class App :Application(){

    companion object {
        lateinit var application: Application;
        fun findContext():Application{
            return application
        }
    }


    override fun onCreate() {
        super.onCreate()
        application = this;
    }
    fun toNight(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}