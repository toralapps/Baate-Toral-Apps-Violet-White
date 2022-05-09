package com.chaatyvideo.navyblue.pink.purple.chat.retrofit


import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.ReportModel
import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
