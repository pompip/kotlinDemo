package com.example.chong.kotlindemo.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.data.articleData.MyArticle
import com.example.chong.kotlindemo.entity.SaveState
import com.example.chong.kotlindemo.util.NetUtil
import com.example.chong.kotlindemo.util.toMarkdownWebActivity
import com.example.chong.kotlindemo.viewMod.MarkdownEditViewModel
import com.example.chong.kotlindemo.viewMod.MyViewModel
import kotlinx.android.synthetic.main.activity_markdown_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarkdownEditActivity : AppCompatActivity() {

    private lateinit var viewModel: MarkdownEditViewModel;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_edit)
        viewModel = ViewModelProviders.of(this).get("article", MarkdownEditViewModel::class.java)
        viewModel.articleLiveData.observe(this, Observer {
            editText.setText(it?.articleContent, TextView.BufferType.EDITABLE);
        })
        viewModel.saveResult.observe(this, Observer {
            if (it!!) {
                Snackbar.make(editText, "保存OK", Snackbar.LENGTH_SHORT).show()
                if (needPreview) {
                    toMarkdownWebActivity(this@MarkdownEditActivity, viewModel.getData().id);
                }
            } else {
                Snackbar.make(editText, "网络异常", Snackbar.LENGTH_SHORT).show()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadArticleById(intent.getLongExtra("content", 0))

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.save -> {
                save(false)
                return true
            }
            R.id.preview -> {
                save(true)
                return true;
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    var needPreview = false;
    fun save(needPreview: Boolean) {
        val article = viewModel.saveData(editText.text.toString())
        viewModel.modifyArticle(article,needPreview)
        this.needPreview = needPreview;
    }
}
