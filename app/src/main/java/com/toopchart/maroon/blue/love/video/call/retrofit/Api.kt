package com.toopchart.maroon.blue.love.video.call.retrofit


import com.toopchart.maroon.blue.love.video.call.videolistmodel.ReportModel
import com.toopchart.maroon.blue.love.video.call.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
