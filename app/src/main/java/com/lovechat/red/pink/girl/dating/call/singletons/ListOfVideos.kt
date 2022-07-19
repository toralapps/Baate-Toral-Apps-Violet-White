package com.lovechat.red.pink.girl.dating.call.singletons

import com.lovechat.red.pink.girl.dating.call.videolistmodel.Data
import com.lovechat.red.pink.girl.dating.call.videolistmodel.ReportModel
import com.lovechat.red.pink.girl.dating.call.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}