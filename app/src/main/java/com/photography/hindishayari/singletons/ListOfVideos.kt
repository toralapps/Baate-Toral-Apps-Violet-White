package com.photography.hindishayari.singletons

import com.photography.hindishayari.videolistmodel.Data
import com.photography.hindishayari.videolistmodel.ReportModel
import com.photography.hindishayari.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}