package com.photography.stylishfonts.singletons

import com.photography.stylishfonts.videolistmodel.Data
import com.photography.stylishfonts.videolistmodel.ReportModel
import com.photography.stylishfonts.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

}