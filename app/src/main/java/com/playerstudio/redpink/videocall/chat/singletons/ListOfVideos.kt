package com.playerstudio.redpink.videocall.chat.singletons

import com.playerstudio.redpink.videocall.chat.videolistmodel.Data
import com.playerstudio.redpink.videocall.chat.videolistmodel.ReportModel
import com.playerstudio.redpink.videocall.chat.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}