package com.nimychat.bottlewhite.videocall.singletons

import com.nimychat.bottlewhite.videocall.videolistmodel.Data
import com.nimychat.bottlewhite.videocall.videolistmodel.ReportModel
import com.nimychat.bottlewhite.videocall.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}