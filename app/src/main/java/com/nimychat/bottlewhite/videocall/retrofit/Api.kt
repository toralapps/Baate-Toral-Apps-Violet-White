package com.nimychat.bottlewhite.videocall.retrofit


import com.nimychat.bottlewhite.videocall.videolistmodel.ReportModel
import com.nimychat.bottlewhite.videocall.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
//    @GET("getimageslist-live.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
