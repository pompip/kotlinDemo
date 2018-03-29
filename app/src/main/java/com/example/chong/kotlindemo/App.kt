package com.example.chong.kotlindemo

import android.app.Application
import android.content.Context
import android.support.v7.app.AppCompatDelegate

class App :Application(){
    companion object {
        val instances = App();
    }
    fun getContext(): Context {
        return getContext();
    }

    override fun onCreate() {
        super.onCreate()
    }
    fun toNight(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}