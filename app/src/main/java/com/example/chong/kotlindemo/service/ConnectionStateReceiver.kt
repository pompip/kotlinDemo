package com.example.chong.kotlindemo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Parcelable
import android.util.Log

class ConnectionStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var isConnectivity= false;
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            loge(this, "wifiState" + wifiState);
            if (WifiManager.WIFI_STATE_ENABLED == wifiState) {
                isConnectivity = true;
            }
        }
        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager
        // .WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
        // 当然刚打开wifi肯定还没有连接到有效的无线
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            val parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO) as Parcelable;
            if (null != parcelableExtra) {
                val networkInfo = parcelableExtra as NetworkInfo;
                val state = networkInfo.getState();
                if (state == NetworkInfo.State.CONNECTED) {
                   isConnectivity = true;
                }
            }
        }
        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
        // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
        // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
            loge(this, "CONNECTIVITY_ACTION");

            val activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        isConnectivity = true
                        loge(this, "当前WiFi连接可用 ");
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to the mobile provider's data plan
                        isConnectivity = true;
                        loge(this, "当前移动网络连接可用 ");
                    }
                } else {
                    loge(this, "当前没有网络连接，请确保你已经打开网络 ");
                }
            }
        }
        if (isConnectivity){
            context.startService(Intent(context, BindingService::class.java))
        }

    }

    fun loge (tag:Any,msg:Any){
        Log.e(tag.javaClass.name,msg.toString())
    }
}
