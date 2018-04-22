package com.example.chong.kotlindemo.entity


data class MsgCmd(
        var cmd: Int,
        var body: String
)

data class MsgEntity(
        var fromUserID: String,
        var toUserID: String,
        var time: Long,
        var msgData: String
) {
    constructor() : this("", "", 0, "")
}

data class ChatUserInfo(var userID: String, var userName: String)