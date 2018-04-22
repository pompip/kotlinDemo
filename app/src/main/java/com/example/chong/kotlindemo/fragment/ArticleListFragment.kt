package com.example.chong.kotlindemo.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.data.articleData.MyArticle
import com.example.chong.kotlindemo.util.toMarkdownEditActivity
import com.example.chong.kotlindemo.util.toMarkdownWebActivity
import com.example.chong.kotlindemo.viewMod.MyViewModel
import com.example.chong.kotlindemo.widget.recorder.MusicListView
import kotlinx.android.synthetic.main.fragment_articleitem.view.*
import kotlinx.android.synthetic.main.fragment_articleitem_list.*

/**
 * Created by chong on 2018/3/27.
 */
class ArticleListFragment : Fragment() {
    lateinit var viewModel: MyViewModel;
    lateinit var adapter: ArticleListAdapter;

    companion object {
        val instances = ArticleListFragment();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders
                .of(activity, ViewModelProvider.AndroidViewModelFactory(activity.application))
                .get("article", MyViewModel::class.java)


        viewModel.articleListLiveData.observe(activity, Observer<List<MyArticle>> {
            adapter.items = it;
            list.adapter.notifyDataSetChanged();
        })
        viewModel.refreshResult.observe(activity, Observer {
            swipe.isRefreshing = false;
        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_articleitem_list, container, false);
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadArticles()

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ArticleListAdapter()
        list.apply {
            this.layoutManager = LinearLayoutManager(context);
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        list.adapter = adapter;

        swipe.setOnRefreshListener {
            viewModel.forceRefresh()
        }


    }

    inner class ArticleListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var items: List<MyArticle>? = viewModel.getArticleList();
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.fragment_articleitem, parent, false)) {}
        }

        override fun getItemCount(): Int {
            return if (items == null) 0 else items?.size!!
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            val myArticle = items?.get(position)!!
            holder?.itemView?.setOnClickListener {
                toMarkdownWebActivity(context, myArticle.id)
            }
            holder?.itemView?.setOnLongClickListener {
                toMarkdownEditActivity(context, myArticle.id);
                return@setOnLongClickListener true;
            }
            holder?.itemView?.article_title?.text = myArticle.articleTitle
            holder?.itemView?.article_time?.text = myArticle.articleTime
            holder?.itemView?.article_content?.text = myArticle.articleContent
        }
    }

}
