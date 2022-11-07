package com.rummychat.lightgreen.livecall.chat.singletons

import com.rummychat.lightgreen.livecall.chat.videolistmodel.Data
import com.rummychat.lightgreen.livecall.chat.videolistmodel.ReportModel
import com.rummychat.lightgreen.livecall.chat.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}