package com.appsplayerstudio.workoutmanager.retrofit


import com.appsplayerstudio.workoutmanager.videolistmodel.ReportModel
import com.appsplayerstudio.workoutmanager.videolistmodel.VideoList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("getimageslist.json")
//    @GET("getimageslist-live.json")
    suspend fun getVideo():Response<VideoList>

    @GET("save.json")
    suspend fun getReport(): Response<ReportModel>
}
