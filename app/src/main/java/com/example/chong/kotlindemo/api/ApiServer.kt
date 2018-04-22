package com.example.chong.kotlindemo.api

import com.example.chong.kotlindemo.data.articleData.MyArticle
import com.example.chong.kotlindemo.entity.SaveState
import retrofit2.Call
import retrofit2.http.*

interface ApiServer{
    @GET("api/findAllArticle/")
    fun allMyArticle( ) :Call<List<MyArticle>>

    @GET("api/findOneArticle/")
    fun findOneArticleByID(@Query("id") id :Long):Call<MyArticle>

    @POST("api/uploadArticle/")
    fun uploadArticle(@Body article: MyArticle):Call<SaveState>;

    @POST("websocket/getUsers/")
    fun getAllUsers():Call<List<String>>;


    @GET("api/findAllArticle/")
    fun allMyArticleData( ) :Call<List<com.example.chong.kotlindemo.data.articleData.MyArticle>>
}
