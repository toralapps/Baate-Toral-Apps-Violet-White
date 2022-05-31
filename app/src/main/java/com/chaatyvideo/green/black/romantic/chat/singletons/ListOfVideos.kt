package com.chaatyvideo.green.black.romantic.chat.singletons

import com.chaatyvideo.green.black.romantic.chat.videolistmodel.Data
import com.chaatyvideo.green.black.romantic.chat.videolistmodel.ReportModel
import com.chaatyvideo.green.black.romantic.chat.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos:Data? = null

}