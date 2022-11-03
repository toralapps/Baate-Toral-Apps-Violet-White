package com.rugbychat.redpink.video.call.singletons

import com.rugbychat.redpink.video.call.videolistmodel.Data
import com.rugbychat.redpink.video.call.videolistmodel.ReportModel
import com.rugbychat.redpink.video.call.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}