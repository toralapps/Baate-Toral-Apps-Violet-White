package com.lovechat.maroon.white.live.vodeo.call.retrofit


import com.lovechat.maroon.white.live.vodeo.call.videolistmodel.ReportModel
import com.lovechat.maroon.white.live.vodeo.call.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
