package com.chatguru.live.meet.video.chat.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lovechat.lightgreen.white.meet.live.chat.repository.Response
import com.lovechat.lightgreen.white.meet.live.chat.retrofit.Api
import com.lovechat.lightgreen.white.meet.live.chat.singletons.ListOfVideos
import com.lovechat.lightgreen.white.meet.live.chat.videolistmodel.ReportModel
import com.lovechat.lightgreen.white.meet.live.chat.videolistmodel.VideoList
import javax.inject.Inject

class Repository @Inject constructor(private val api: Api) {

    val response=MutableLiveData<Response<VideoList>>()
    val liveDataVideo:LiveData<Response<VideoList>> get() = response

    suspend fun getVideos() {

        response.value = Response.Loading()

        var result:retrofit2.Response<VideoList>? = null
        try {
            if (ListOfVideos.Store == null) {
                result = api.getVideo()
                ListOfVideos.Store =result
            }
            else{
                result = ListOfVideos.Store
            }

       if(result!!.isSuccessful && result.body()!= null && result.body()!!.Data.isNotEmpty()){
           response.value = Response.Success(result.body())

       }else{

           response.value = Response.error("Please try after sometimes")
       }

    }catch (e:Exception){

        response.value = Response.error("Please try after sometimes")

        }
    }

  suspend fun getReport(): Response<ReportModel> {
      var report:retrofit2.Response<ReportModel>? = null
      return try{
          if (ListOfVideos.storeReport == null){
              report = api.getReport()
              ListOfVideos.storeReport = report
          }
          else{
              report = ListOfVideos.storeReport
          }
          if (report!!.isSuccessful && report.body() != null){
              Log.d("DEEP","running")
              Response.Success(report.body())
          }else{
              Log.d("deep", report.message().toString())
              Response.error("Please try after sometimes")
          }
      }catch (e:Exception){
          Log.d("DEEP",e.message.toString())
          Response.error("Please try after sometimes")
      }
    }
//
//     val api1 = Retrofit.Builder()
//          .baseUrl("https://printf-app-data.github.io/Video-Chat-Test-API/")
//          .addConverterFactory(GsonConverterFactory.create())
//          .build()
//
//
//      try{
//         api1.create(Api::class.java).getReport().enqueue(object :Callback<ReportModel>{
//             override fun onResponse(
//                 call: Call<ReportModel>,
//                 response: retrofit2.Response<ReportModel>
//             ) {
//                 if (response.isSuccessful){
//                     Log.d("DEEP","SuccessFul${response.body()}")
//                 }
//                 else{
//                     Log.d("DEEP","Failed")
//                 }
//             }
//
//             override fun onFailure(call: Call<ReportModel>, t: Throwable) {
//                 Log.d("DEEP","Failed${t.printStackTrace()}")
//             }
//
//         })
//          }catch (e:Exception){
//                Log.d("DEEP",e.message.toString())
//     }
//     return Response.error("Susu")
//      }

}