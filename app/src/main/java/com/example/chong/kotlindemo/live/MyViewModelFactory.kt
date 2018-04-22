package com.example.chong.kotlindemo.live

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider


class MyViewModelFactory: ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return super.create(modelClass)
    }
}