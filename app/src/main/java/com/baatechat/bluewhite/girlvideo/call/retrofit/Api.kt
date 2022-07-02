package com.baatechat.bluewhite.girlvideo.call.retrofit


import com.baatechat.bluewhite.girlvideo.call.videolistmodel.ReportModel
import com.baatechat.bluewhite.girlvideo.call.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
