package com.example.chong.kotlindemo.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.chong.kotlindemo.entity.MsgUser

object DaoUtil {


    val tableName = "msg"
    fun insert(context: Context, msgUser: MsgUser) {
        val helper = DaoHelper(context);
        val database = helper.writableDatabase;
        val values = ContentValues()
        values.put("fromUserID", msgUser.fromUserID)
        values.put("toUserID", msgUser.toUserID)
        values.put("time", msgUser.time)
        values.put("msgData", msgUser.msgData)
        database.insert(tableName, null, values)
        database.close();
    }

    fun query(context: Context, fromUserID:String): List<MsgUser> {
        val helper = DaoHelper(context);
        val database = helper.readableDatabase;
        val cursor = database.rawQuery("select * from msg where fromUserID = ?", arrayOf(fromUserID))
        val list = ArrayList<MsgUser>()
        while (cursor.moveToNext()) {
            val msg = MsgUser()
            msg.fromUserID = cursor.getString(1);
            msg.toUserID = cursor.getString(2);
            msg.time = cursor.getLong(3);
            msg.msgData = cursor.getString(4);
            list.add(msg)
        }
        cursor.close();
        return list;
    }

    class DaoHelper(context: Context) : SQLiteOpenHelper(context, "my_msg", null, 1) {

        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL("create table msg (_id Integer primary key autoincrement ,fromUserID,toUserID ,time,msgData)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        }

    }
}