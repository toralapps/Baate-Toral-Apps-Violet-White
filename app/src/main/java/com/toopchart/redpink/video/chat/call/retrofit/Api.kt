package com.toopchart.redpink.video.chat.call.retrofit


import com.toopchart.redpink.video.chat.call.videolistmodel.ReportModel
import com.toopchart.redpink.video.chat.call.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
