package com.example.chong.kotlindemo.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.api.ApiServer
import com.example.chong.kotlindemo.entity.MyArticle
import com.example.chong.kotlindemo.util.NetUtil
import com.example.chong.kotlindemo.util.toMarkdownEditActivity
import com.example.chong.kotlindemo.util.toMarkdownWebActivity
import kotlinx.android.synthetic.main.fragment_articleitem_list.*
import kotlinx.android.synthetic.main.fragment_articleitem.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by chong on 2018/3/27.
 */
class ArticleListFragment : Fragment() {
    val array = ArrayList<MyArticle>();

    companion object {
        val instances = ArticleListFragment();
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_articleitem_list, container, false);
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        list.apply {
            layoutManager = LinearLayoutManager(context);
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                    return object : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.fragment_articleitem, parent, false)) {}
                }

                override fun getItemCount(): Int {
                    return array.size;
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                    val myArticle = array[position]
                    holder?.itemView?.setOnClickListener {
                        toMarkdownWebActivity(context, myArticle)
                    }
                    holder?.itemView?.setOnLongClickListener {
                        toMarkdownEditActivity(context, myArticle);
                        return@setOnLongClickListener true;
                    }
                    holder?.itemView?.article_title?.text = myArticle.articleTitle
                    holder?.itemView?.article_time?.text = myArticle.articleTime
                    holder?.itemView?.article_content?.text = myArticle.articleBrief
                }
            }
        }
        swipe.setOnRefreshListener {
            NetUtil.apiServer.allMyArticle().enqueue(object : Callback<List<MyArticle>> {
                override fun onFailure(call: Call<List<MyArticle>>?, t: Throwable?) {
                    t?.printStackTrace()
                    swipe.isRefreshing = false;
                }

                override fun onResponse(call: Call<List<MyArticle>>?, response: Response<List<MyArticle>>?) {
                    val elements = response?.body()
                    array.clear();
                    array.addAll(elements!!)
                    list.adapter.notifyDataSetChanged();
                    swipe.isRefreshing = false;

                }

            });
        }
        NetUtil.apiServer.allMyArticle() .enqueue(object : Callback<List<MyArticle>> {
            override fun onFailure(call: Call<List<MyArticle>>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<List<MyArticle>>?, response: Response<List<MyArticle>>?) {
                val elements = response?.body()
                array.addAll(elements!!)
                list.adapter.notifyDataSetChanged();
            }

        });

    }


}
