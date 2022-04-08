package com.trendingchat.love.pink.video.chat.singletons

import com.trendingchat.love.pink.video.chat.videolistmodel.Data
import com.trendingchat.love.pink.video.chat.videolistmodel.ReportModel
import com.trendingchat.love.pink.video.chat.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos:Data? = null

}