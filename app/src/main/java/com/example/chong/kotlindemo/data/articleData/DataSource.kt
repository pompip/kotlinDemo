package com.example.chong.kotlindemo.data.articleData

import android.content.Context
import com.example.chong.kotlindemo.util.NetUtil
import java.io.IOException

interface DataSource {
    fun getArticles(callback: DataSourceCallback<List<MyArticle>>)
    fun deleteAllArticles()
    fun insertArticle(article: MyArticle)
    fun findArticleById( id:Long,callback: DataSourceCallback<MyArticle>);
    fun modifyArticle(article: MyArticle,callback: DataSourceCallback<MyArticle>)
}

class LocalDataSource : DataSource {
    override fun modifyArticle(article: MyArticle, callback: DataSourceCallback<MyArticle>) {
        myExecutors.IO.execute{
            val result = database.articleDao().modifyArticle(article)
            myExecutors.UI.execute {
                if (result==1){
                    callback.onTasksLoaded(article)
                }else{
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun findArticleById(id: Long,callback:DataSourceCallback<MyArticle>) {
        myExecutors.IO.execute {
            val myArticle = database.articleDao().findArticleById(id);
            myExecutors.UI.execute {
                if (myArticle==null){
                    callback.onDataNotAvailable()
                }else{
                    callback.onTasksLoaded(myArticle)
                }
            }
        }
    }

    override fun insertArticle(article: MyArticle) {
        myExecutors.IO.execute {
            database.articleDao().insertArticle(article)
        }
    }

    override fun deleteAllArticles() {
        myExecutors.IO.execute {
            database.articleDao().deleteAllArticles()
        }
    }

    val database: ArticleDatabase;

    constructor(context: Context) {
        database = ArticleDatabase.getInstance(context);
    }

    private val myExecutors = MyExecutors()
    override fun getArticles(callback: DataSourceCallback<List<MyArticle>>) {
        myExecutors.IO.execute {
            val articles = database.articleDao().getArticles()
            myExecutors.UI.execute {
                if (articles.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTasksLoaded(articles)
                }
            }
        }
    }
}

class RemoteDataSource : DataSource {
    override fun modifyArticle(article: MyArticle, callback: DataSourceCallback<MyArticle>) {
        myExecutors.NET.execute {
            val body = NetUtil.apiServer.uploadArticle(article).execute().body()
            myExecutors.UI.execute {
                if (body==null||body.state!="success"){
                    callback.onDataNotAvailable()
                }else{
                    callback.onTasksLoaded(article);
                }
            }
        }
    }

    override fun insertArticle(article: MyArticle) {
    }

    override fun deleteAllArticles() {
    }

    override fun findArticleById(id: Long,callback: DataSourceCallback<MyArticle>) {
        myExecutors.NET.execute {
            val body = NetUtil.apiServer.findOneArticleByID(id).execute().body();
            myExecutors.UI.execute {
                if (body==null){
                    callback.onDataNotAvailable()
                }else{
                    callback.onTasksLoaded(body)
                }
            }
        }
    }
    private val myExecutors = MyExecutors()
    override fun getArticles(callback: DataSourceCallback<List<MyArticle>>) {
        myExecutors.NET.execute {
            var body: List<MyArticle>?;
            try {
                val response = NetUtil.apiServer.allMyArticleData().execute()
                body = response.body()
            } catch (e: IOException) {
                body = null;
            }
            myExecutors.UI.execute {
                if (body == null || body.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTasksLoaded(body)
                }
            }
        }
    }
}
interface DataSourceCallback<T>{
    fun onTasksLoaded(data: T)

    fun onDataNotAvailable()
}
