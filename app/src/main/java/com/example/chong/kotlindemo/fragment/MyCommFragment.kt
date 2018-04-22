package com.example.chong.kotlindemo.fragment

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.util.NetUtil
import com.example.chong.kotlindemo.util.toChatActivity
import com.example.chong.kotlindemo.viewMod.MyViewModel
import kotlinx.android.synthetic.main.fragment_my_comm.*
import kotlinx.android.synthetic.main.item_my_comm.view.*
import retrofit2.Call
import retrofit2.Response

/**
 * Created by chong on 2018/3/24.
 */
class MyCommFragment : Fragment(), retrofit2.Callback<List<String>> {
    override fun onFailure(call: Call<List<String>>?, t: Throwable?) {

    }

    override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>?) {
        userList.clear()
        userList.add(Pair("all", 0))
        val body = response?.body()
        body?.forEach {
            userList.add(Pair(it, 0))
        }
        recycle_view_my_comm.adapter.notifyDataSetChanged();
        conn_swipe.isRefreshing = false
    }

    companion object {
        val instances = MyCommFragment();
    }
    val userList = ArrayList<Pair<String, Int>>();
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_my_comm, container, false)!!;
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycle_view_my_comm.apply {
            adapter = MyCommAdapter();
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
        }
        conn_swipe.setOnRefreshListener {
            requestUsers();
        }
    }

    private fun requestUsers() {
        NetUtil.apiServer.getAllUsers().enqueue(this)
    }

    override fun onResume() {
        super.onResume()
        requestUsers()
    }

    override fun onPause() {
        super.onPause()
    }


    inner class MyCommAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_comm, parent, false)) {}
        }

        override fun getItemCount(): Int {
            return userList.size;
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            val pair = userList[position]
            holder?.itemView?.setOnClickListener {
                toChatActivity(context, pair.first)
            }
            holder?.itemView?.text_user_name?.text = pair.first
            holder?.itemView?.text_msg_num?.text = pair.second.toString()
        }
    }

}