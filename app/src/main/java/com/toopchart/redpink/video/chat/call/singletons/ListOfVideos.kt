package com.toopchart.redpink.video.chat.call.singletons

import com.toopchart.redpink.video.chat.call.videolistmodel.Data
import com.toopchart.redpink.video.chat.call.videolistmodel.ReportModel
import com.toopchart.redpink.video.chat.call.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}