package com.example.chong.kotlindemo.activity

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.data.articleData.MyArticle
import com.example.chong.kotlindemo.util.toMarkdownEditActivity
import com.example.chong.kotlindemo.viewMod.MyViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_markdown_web.*


class MarkdownWebActivity : AppCompatActivity() {

    lateinit var viewModel :MyViewModel;
    var article:MyArticle?=null;

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_web);
        markdown_web.settings.apply {
            javaScriptEnabled = true;
            allowContentAccess = false;
        }
        viewModel = ViewModelProviders
                .of(this, ViewModelProvider.AndroidViewModelFactory(application))
                .get("article", MyViewModel::class.java)
        viewModel.articleLiveData.observe(this, Observer {
            markdown_web.loadUrl("file:///android_asset/markdownParse.html")
            markdown_web.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    article = it;
                    markdown_web.loadUrl("javascript:parseMarkdown(" + Gson().toJson(it) + ")")
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadArticleById(intent.getLongExtra("content",0) )


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent;
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit -> {
                toMarkdownEditActivity(this, article?.id!!)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
