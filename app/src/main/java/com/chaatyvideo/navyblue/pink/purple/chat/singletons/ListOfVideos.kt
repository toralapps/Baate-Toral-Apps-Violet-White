package com.chaatyvideo.navyblue.pink.purple.chat.singletons

import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.Data
import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.ReportModel
import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos:Data? = null

}