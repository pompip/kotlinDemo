package com.example.chong.kotlindemo.entity

/**
 * Created by chong on 2018/3/20.
 */
data class MsgEntity(var cmd: Int,//1.chatMessage;2.refreshOnlineUser
                     var id: Long,
                     var fromTime: Long,
                     var fromUser: UserEntity,
                     var toUser: UserEntity,
                     var userOnline: UserList) {
}

data class UserEntity(var name: String, var id: Long)

data class UserList(var userList: ArrayList<UserEntity>)