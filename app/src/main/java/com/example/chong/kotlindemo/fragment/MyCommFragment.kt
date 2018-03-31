package com.example.chong.kotlindemo.fragment

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.util.toChatActivity
import kotlinx.android.synthetic.main.fragment_my_comm.*
import kotlinx.android.synthetic.main.item_my_comm.view.*

/**
 * Created by chong on 2018/3/24.
 */
class MyCommFragment : Fragment() {
    companion object {
        val instances = MyCommFragment();
    }


    val userList = ArrayList<String>();
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_my_comm, container, false)!!;
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycle_view_my_comm.apply{
            adapter = MyCommAdapter();
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
        }
    }

    fun newComm(list: List<String>) {
        userList.clear()
        userList.addAll(list)
        if (isVisible){
            recycle_view_my_comm.adapter.notifyDataSetChanged();
        }
    }

    override fun onResume() {
        super.onResume()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        notificationManager.cancelAll()
        recycle_view_my_comm.adapter.notifyDataSetChanged();
    }


    inner class MyCommAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_comm, parent, false)){}
        }

        override fun getItemCount(): Int {
            return userList.size;
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            val userID = userList[position]
            holder?.itemView?.setOnClickListener {
                toChatActivity(context,userID)
            }
            holder?.itemView?.text_user_name?.text = userID


        }

    }

}