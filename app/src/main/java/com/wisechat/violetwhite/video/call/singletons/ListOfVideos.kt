package com.wisechat.violetwhite.video.call.singletons

import android.util.Log
import com.wisechat.violetwhite.video.call.videolistmodel.Data
import com.wisechat.violetwhite.video.call.videolistmodel.ReportModel
import com.wisechat.violetwhite.video.call.videolistmodel.VideoList
import retrofit2.Response
import kotlin.math.log

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

     val ListOfApis = listOf<String>(
          "https://printf-app-data.github.io/Video-Chat-Test-API/getimageslist-live.json",
         "https://codeberg.org/printf-app-data/pages/raw/branch/main/Video-Chat-Test-API/getimageslist-live.json",
          "https://codeberg.org/printf-app-data/pages/raw/branch/main/Video-Chat-Test-API/save.json",
          "https://codeberg.org/printf-app-data/pages/raw/branch/main/Ad-Test-API/test-ads-true.json",
     )

     var selectedIndex = 0
     var selectedApi = ListOfApis[selectedIndex]
     fun setNextApi(){
          selectedIndex += 1
          if (selectedIndex != ListOfApis.size){
               Log.d("RECALLINGAPI", "setNextApi: ")
               selectedApi = ListOfApis[selectedIndex]
          }
     }
}