package com.example.chong.kotlindemo.data.articleData

import com.example.chong.kotlindemo.App
import java.util.*

object ArticleRepository : DataSource {
    private val localDataSource = LocalDataSource(App.application)
    private val remoteDataSource = RemoteDataSource();
    private val articleMap = TreeMap<Long, MyArticle>(kotlin.Comparator { o1, o2 ->
        return@Comparator o2.compareTo(o1)
    });
    var forceRefresh = false;


    override fun modifyArticle(article: MyArticle, callback: DataSourceCallback<MyArticle>) {
        articleMap.put(article.id,article)
        remoteDataSource.modifyArticle(article,object :DataSourceCallback<MyArticle>{
            override fun onTasksLoaded(data: MyArticle) {
                localDataSource.modifyArticle(data,callback)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })
    }

    override fun findArticleById(id: Long, callback: DataSourceCallback<MyArticle>) {
        val myArticle = articleMap[id]
        if (myArticle == null) {
            localDataSource.findArticleById(id, object : DataSourceCallback<MyArticle> {
                override fun onTasksLoaded(myArticle: MyArticle) {
                    articleMap.put(myArticle.id, myArticle)
                    callback.onTasksLoaded(myArticle)
                }

                override fun onDataNotAvailable() {
                    remoteDataSource.findArticleById(id, object : DataSourceCallback<MyArticle> {
                        override fun onTasksLoaded(myArticle: MyArticle) {

                        }

                        override fun onDataNotAvailable() {

                        }

                    })
                }

            })
        } else {
            callback.onTasksLoaded(myArticle)
        }
    }

    override fun insertArticle(article: MyArticle) {

    }

    override fun deleteAllArticles() {
    }


    override fun getArticles(callback: DataSourceCallback<List<MyArticle>>) {
        if (forceRefresh) {
            getArticlesFromRemote(callback)
        } else {
            if (articleMap.values.isEmpty()) {
                localDataSource.getArticles(object : DataSourceCallback<List<MyArticle>> {
                    override fun onTasksLoaded(data: List<MyArticle>) {
                        refreshCacheMap(data)
                        callback.onTasksLoaded(ArrayList(articleMap.values))
                    }

                    override fun onDataNotAvailable() {
                        getArticlesFromRemote(callback)
                    }
                })
            } else {
                callback.onTasksLoaded(ArrayList(articleMap.values))
            }
        }
    }
    private fun refreshCacheMap(articles:List<MyArticle>){
        articleMap.clear()
        articles.forEach {
            articleMap.put(it.id, it);
            localDataSource.insertArticle(it)
        }
    }

    private fun getArticlesFromRemote(callback: DataSourceCallback<List<MyArticle>>) {
        remoteDataSource.getArticles(object : DataSourceCallback<List<MyArticle>> {
            override fun onTasksLoaded(data: List<MyArticle>) {
                localDataSource.deleteAllArticles()
                refreshCacheMap(data)
                callback.onTasksLoaded(ArrayList(articleMap.values))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }
}