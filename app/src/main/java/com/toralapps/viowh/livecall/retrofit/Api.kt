package com.toralapps.viowh.livecall.retrofit


import com.toralapps.viowh.livecall.videolistmodel.ReportModel
import com.toralapps.viowh.livecall.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface Api {
    @GET
//    @GET("getimageslist-live.json")
    suspend fun getVideo(@Url fullUrl:String):Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
