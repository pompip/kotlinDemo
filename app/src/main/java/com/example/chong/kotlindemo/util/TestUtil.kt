package com.example.chong.kotlindemo.util

import com.example.chong.kotlindemo.entity.ChatMsg

/**
 * Created by Chong on 2018/3/8.
 */
fun getChatMsg( i:Int):ChatMsg{
    return ChatMsg("title $i","content $i","http://",System.currentTimeMillis())
}