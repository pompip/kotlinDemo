package com.example.chong.kotlindemo.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatDelegate
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.entity.MyArticle
import com.example.chong.kotlindemo.entity.SaveState
import com.example.chong.kotlindemo.util.NetUtil
import com.example.chong.kotlindemo.util.toMarkdownWebActivity
import kotlinx.android.synthetic.main.activity_markdown_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarkdownEditActivity : AppCompatActivity() {

    private val myArticle: MyArticle
        get() {
            return intent.getSerializableExtra("content") as MyArticle
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_edit)


    }

    override fun onResume() {
        super.onResume()
        editText.setText(myArticle.articleContent,TextView.BufferType.EDITABLE) ;
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
            R.id.preview ->{
                save(true)
                return true;
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun save(needPreview :Boolean){
        myArticle.articleContent = editText.text.toString();
         NetUtil.apiServer.uploadArticle(myArticle).enqueue(object :Callback<SaveState>{
             override fun onFailure(call: Call<SaveState>?, t: Throwable?) {
                Snackbar.make(editText,"网络异常",Snackbar.LENGTH_SHORT)
             }

             override fun onResponse(call: Call<SaveState>?, response: Response<SaveState>?) {
                 if (needPreview){
                     toMarkdownWebActivity(this@MarkdownEditActivity ,myArticle);
                 }
             }
         })
    }
}
