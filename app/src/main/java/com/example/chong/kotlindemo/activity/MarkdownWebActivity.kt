package com.example.chong.kotlindemo.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.entity.MyArticle
import com.example.chong.kotlindemo.util.toMarkdownEditActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_markdown_web.*


class MarkdownWebActivity : AppCompatActivity() {

    private val myArticle: MyArticle
        get() {
            return intent.getSerializableExtra("content") as MyArticle
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_web);

    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()
        title = myArticle.articleTitle;
        markdown_web.settings.apply {
            javaScriptEnabled = true;
            allowContentAccess =false;
        }
        markdown_web.loadUrl("file:///android_asset/markdownParse.html")
        markdown_web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                markdown_web.loadUrl("javascript:parseMarkdown("+ Gson().toJson(myArticle) +")")
            }
        }
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
                toMarkdownEditActivity(this ,myArticle )
                return true
            }else -> super.onOptionsItemSelected(item)
        }
    }
}
