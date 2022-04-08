package com.trendingchat.love.pink.video.chat.retrofit


import com.trendingchat.love.pink.video.chat.videolistmodel.ReportModel
import com.trendingchat.love.pink.video.chat.videolistmodel.VideoList
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
