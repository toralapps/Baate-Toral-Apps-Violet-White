package com.rugbychat.redpink.video.call.retrofit


import com.rugbychat.redpink.video.call.videolistmodel.ReportModel
import com.rugbychat.redpink.video.call.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
//    @GET("getimageslist-live.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}