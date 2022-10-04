package com.appsplayerstudio.workoutmanager.singletons

import com.appsplayerstudio.workoutmanager.videolistmodel.Data
import com.appsplayerstudio.workoutmanager.videolistmodel.ReportModel
import com.appsplayerstudio.workoutmanager.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}