package com.example.chong.kotlindemo.entity

import java.io.Serializable

data class MyArticle(var id:Long,
                     var articleTitle:String,
                     var articleTime :String,
                     var articleBrief:String,
                     var articleContent:String):Serializable;

data class SaveState(var id:Long,var state:String);