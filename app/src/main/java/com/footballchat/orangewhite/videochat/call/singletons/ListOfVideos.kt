package com.footballchat.orangewhite.videochat.call.singletons

import com.footballchat.orangewhite.videochat.call.videolistmodel.Data
import com.footballchat.orangewhite.videochat.call.videolistmodel.ReportModel
import com.footballchat.orangewhite.videochat.call.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}