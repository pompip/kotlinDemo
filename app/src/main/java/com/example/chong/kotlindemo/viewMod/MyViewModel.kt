package com.example.chong.kotlindemo.viewMod

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.example.chong.kotlindemo.data.articleData.*

class MyViewModel(application: Application) : AndroidViewModel(application) {
//    private val localRepository: ArticleRepository = ArticleRepository;

    val articleListLiveData = MutableLiveData<List<MyArticle>>();
    val articleLiveData = MutableLiveData<MyArticle>();

    var refreshResult = MutableLiveData<Boolean>()
    fun loadArticleById(id: Long) {
        ArticleRepository.findArticleById(id,object : DataSourceCallback<MyArticle> {
            override fun onTasksLoaded(myArticle: MyArticle) {
                articleLiveData.value = myArticle;
            }

            override fun onDataNotAvailable() {

            }

        })
    }


    fun forceRefresh() {
        ArticleRepository.forceRefresh = true;
        loadArticles()
    }

    fun loadArticles() {
        ArticleRepository.getArticles(object : DataSourceCallback<List<MyArticle>> {
            override fun onTasksLoaded(myArticles: List<MyArticle>) {
                ArticleRepository.forceRefresh = false;
                articleListLiveData.value = myArticles
                refreshResult.value = true;
            }

            override fun onDataNotAvailable() {
                ArticleRepository.forceRefresh = false;
                refreshResult.value = false;
            }

        })
    }

    fun getArticleList(): List<MyArticle>? {
        val value = articleListLiveData.value
        return value
    }
}