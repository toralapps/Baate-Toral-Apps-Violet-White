package com.toralapps.viowh.livecall.singletons

import android.util.Log
import com.toralapps.viowh.livecall.videolistmodel.Data
import com.toralapps.viowh.livecall.videolistmodel.ReportModel
import com.toralapps.viowh.livecall.videolistmodel.VideoList
import retrofit2.Response

object ListOfVideos {


     var Store:Response<VideoList>? = null

     var storeReport:Response<ReportModel>? = null


     var videos: Data? = null

     val ListOfApis = listOf<String>(
          "https://toralapps.github.io/Data/baate-violet-white-chat/getimageslist.json",
                  "https://codeberg.org/toralapps/toralapps.github.io/raw/branch/main/Data/baate-violet-white-chat/getimageslist.json",
                  "https://skytechapps.s3.ap-south-1.amazonaws.com/toralapps.github.io/Data/baate-violet-white-chat/getimageslist.json",
                  "https://raw.githubusercontent.com/toralapps/toralapps.github.io/main/Data/baate-violet-white-chat/getimageslist.json",
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