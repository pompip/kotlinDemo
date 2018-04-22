package com.example.chong.kotlindemo.data.articleData

import android.arch.persistence.room.*
import android.content.Context
import java.io.Serializable

@Entity(tableName = "myArticle")
data class MyArticle(
        @PrimaryKey @ColumnInfo(name = "articleId") var id: Long,
        @ColumnInfo(name = "articleTitle") var articleTitle: String,
        @ColumnInfo(name = "articleTime") var articleTime: String,
        @ColumnInfo(name = "articleBrief") var articleBrief: String,
        @ColumnInfo(name = "articleContent") var articleContent: String) : Serializable;

@Dao
interface ArticleDao {
    @Query("SELECT * FROM myArticle")
    fun getArticles(): List<MyArticle>
    @Query("DELETE FROM myArticle")
    fun deleteAllArticles();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: MyArticle)
    @Query("SELECT * From myArticle WHERE articleId = :id")
    fun findArticleById(id:Long):MyArticle?;
    @Update
    fun modifyArticle(article: MyArticle):Int
}

@Database(entities = [(MyArticle::class)], version = 1)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    companion object {
        private var INSTANCE: ArticleDatabase? = null;
        fun getInstance(context: Context): ArticleDatabase {
            synchronized(RoomDatabase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            ArticleDatabase::class.java, "MyArticle.db") .build()
                }
            }
            return INSTANCE!!
        }
    }
}


