package com.photography.hindishayari.retrofit


import com.photography.hindishayari.videolistmodel.ReportModel
import com.photography.hindishayari.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
