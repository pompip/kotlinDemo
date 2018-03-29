package com.example.chong.kotlindemo.util

import android.annotation.SuppressLint
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

fun Context.logd(msg: Any) {
    Log.d(this.packageName, msg.toString())
}

fun Context.loge(msg: Any) {
    Log.e(this.packageName, msg.toString())
}

@SuppressLint("RestrictedApi")
fun BottomNavigationItemView.setShift(){
    this.setShiftingMode(false)
}