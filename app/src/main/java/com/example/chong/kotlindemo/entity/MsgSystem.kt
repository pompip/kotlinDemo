package com.example.chong.kotlindemo.entity

import android.os.Parcel
import android.os.Parcelable

data class MsgSystem(
        var cmd: Int,
        var time: Long,
        var cmdDes: String,
        var meID: String,
        var onlineUser: List<String>
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArrayList()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cmd)
        parcel.writeLong(time)
        parcel.writeString(cmdDes)
        parcel.writeString(meID)
        parcel.writeStringList(onlineUser)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MsgSystem> {
        override fun createFromParcel(parcel: Parcel): MsgSystem {
            return MsgSystem(parcel)
        }

        override fun newArray(size: Int): Array<MsgSystem?> {
            return arrayOfNulls(size)
        }
    }
}

data class MsgUser(
        var cmd: Int,
        var fromUserID: String,
        var toUserID: String,
        var time: Long,
        var msgData: String
):Parcelable {
    constructor() : this(0,"","",System.currentTimeMillis(),"") {

    }
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cmd)
        parcel.writeString(fromUserID)
        parcel.writeString(toUserID)
        parcel.writeLong(time)
        parcel.writeString(msgData)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MsgUser> {
        override fun createFromParcel(parcel: Parcel): MsgUser {
            return MsgUser(parcel)
        }

        override fun newArray(size: Int): Array<MsgUser?> {
            return arrayOfNulls(size)
        }
    }
}