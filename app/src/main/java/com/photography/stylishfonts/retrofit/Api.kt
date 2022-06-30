package com.photography.stylishfonts.retrofit


import com.photography.stylishfonts.videolistmodel.ReportModel
import com.photography.stylishfonts.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
