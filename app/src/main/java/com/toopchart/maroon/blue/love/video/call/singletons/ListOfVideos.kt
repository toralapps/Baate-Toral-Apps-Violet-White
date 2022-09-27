package com.toopchart.maroon.blue.love.video.call.singletons

import com.toopchart.maroon.blue.love.video.call.videolistmodel.Data
import com.toopchart.maroon.blue.love.video.call.videolistmodel.ReportModel
import com.toopchart.maroon.blue.love.video.call.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}