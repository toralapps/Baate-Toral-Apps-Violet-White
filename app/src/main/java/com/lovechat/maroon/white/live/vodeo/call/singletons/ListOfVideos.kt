package com.lovechat.maroon.white.live.vodeo.call.singletons

import com.lovechat.maroon.white.live.vodeo.call.videolistmodel.Data
import com.lovechat.maroon.white.live.vodeo.call.videolistmodel.ReportModel
import com.lovechat.maroon.white.live.vodeo.call.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}