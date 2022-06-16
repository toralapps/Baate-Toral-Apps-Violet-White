package com.baatechat.blackwhite.swip.call.retrofit


import com.baatechat.blackwhite.swip.call.videolistmodel.ReportModel
import com.baatechat.blackwhite.swip.call.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
