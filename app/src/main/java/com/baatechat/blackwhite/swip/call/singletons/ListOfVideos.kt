package com.baatechat.blackwhite.swip.call.singletons

import com.baatechat.blackwhite.swip.call.videolistmodel.Data
import com.baatechat.blackwhite.swip.call.videolistmodel.ReportModel
import com.baatechat.blackwhite.swip.call.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}