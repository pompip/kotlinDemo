package com.example.chong.kotlindemo.viewMod

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.chong.kotlindemo.data.articleData.ArticleRepository
import com.example.chong.kotlindemo.data.articleData.DataSourceCallback
import com.example.chong.kotlindemo.data.articleData.MyArticle
import com.example.chong.kotlindemo.util.toMarkdownWebActivity

class MarkdownEditViewModel : ViewModel() {
    val articleLiveData = MutableLiveData<MyArticle>();
    val saveResult = MutableLiveData<Boolean>();
    private var article: MyArticle? = null;
    fun loadArticleById(id: Long) {
        ArticleRepository.findArticleById(id, object : DataSourceCallback<MyArticle> {
            override fun onTasksLoaded(myArticle: MyArticle) {
                articleLiveData.value = myArticle;
                article = myArticle;
            }

            override fun onDataNotAvailable() {

            }

        })
    }

    fun modifyArticle(article: MyArticle,needPreview:Boolean=false) {
        ArticleRepository.modifyArticle(article, object : DataSourceCallback<MyArticle> {
            override fun onTasksLoaded(data: MyArticle) {
                articleLiveData.value = data;
                saveResult.value =true

            }

            override fun onDataNotAvailable() {
                saveResult.value =false
            }
        })
    }

    fun saveData(content: String): MyArticle {
        article?.articleContent = content;
        return article!!;
    }

    fun getData():MyArticle{
        return article!!;
    }
}