package com.example.chong.kotlindemo.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.support.design.internal.BottomNavigationItemView
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Toast

/**
 * Created by Chong on 2018/3/8.
 */

fun Context.toast(toast: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, toast, length).show();
}

fun Fragment.toast(toast: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.context, toast, length).show();
}


fun Context.loge(clazz: Any, msg: Any) {
    Log.e(clazz.javaClass.name, msg.toString())
}

@SuppressLint("RestrictedApi")
fun BottomNavigationItemView.setShift() {
    this.setShiftingMode(false)
}



fun formattedTime(second: Long, needHH: Boolean = true): String {
    val h: Long = second / 3600
    val m: Long = if (needHH) second % 3600 / 60 else second / 60;
    val s: Long = second % 3600 % 60
    val hs = if (needHH) if (h < 10) "0$h:" else "$h:" else ""
    val ms: String = if (m < 10) "0$m" else "$m"
    val ss = if (s < 10) "0$s" else "$s"
    return "$hs$ms:$ss"
}