package com.baatechat.bluewhite.girlvideo.call.singletons

import com.baatechat.bluewhite.girlvideo.call.videolistmodel.Data
import com.baatechat.bluewhite.girlvideo.call.videolistmodel.ReportModel
import com.baatechat.bluewhite.girlvideo.call.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}