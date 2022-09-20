package com.footballchat.orangewhite.videochat.call.retrofit


import com.footballchat.orangewhite.videochat.call.videolistmodel.ReportModel
import com.footballchat.orangewhite.videochat.call.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
