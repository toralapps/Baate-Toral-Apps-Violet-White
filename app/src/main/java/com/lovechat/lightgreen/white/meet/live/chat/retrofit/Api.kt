package com.lovechat.lightgreen.white.meet.live.chat.retrofit


import com.lovechat.lightgreen.white.meet.live.chat.videolistmodel.ReportModel
import com.lovechat.lightgreen.white.meet.live.chat.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
