package com.example.chong.kotlindemo.data.articleData

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MyExecutors {
    var IO: Executor;
    var NET: Executor;
    var UI: Executor;

    init {
        this.IO = Executors.newSingleThreadExecutor();
        this.NET = Executors.newFixedThreadPool(3);
        this.UI = UIExecute();
    }


    class UIExecute : Executor {
        val handler: Handler = Handler(Looper.getMainLooper());
        override fun execute(command: Runnable?) {
            handler.post(command)
        }

    }

}