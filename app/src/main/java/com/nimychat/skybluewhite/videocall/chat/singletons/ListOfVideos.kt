package com.nimychat.skybluewhite.videocall.chat.singletons

import com.nimychat.skybluewhite.videocall.chat.videolistmodel.Data
import com.nimychat.skybluewhite.videocall.chat.videolistmodel.ReportModel
import com.nimychat.skybluewhite.videocall.chat.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}