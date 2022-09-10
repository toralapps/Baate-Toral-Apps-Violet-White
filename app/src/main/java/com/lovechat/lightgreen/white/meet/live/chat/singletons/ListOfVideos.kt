package com.lovechat.lightgreen.white.meet.live.chat.singletons

import com.lovechat.lightgreen.white.meet.live.chat.videolistmodel.Data
import com.lovechat.lightgreen.white.meet.live.chat.videolistmodel.ReportModel
import com.lovechat.lightgreen.white.meet.live.chat.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}