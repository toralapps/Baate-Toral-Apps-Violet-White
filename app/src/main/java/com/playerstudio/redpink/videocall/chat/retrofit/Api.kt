package com.playerstudio.redpink.videocall.chat.retrofit


import com.playerstudio.redpink.videocall.chat.videolistmodel.ReportModel
import com.playerstudio.redpink.videocall.chat.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
//    @GET("getimageslist-live.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
