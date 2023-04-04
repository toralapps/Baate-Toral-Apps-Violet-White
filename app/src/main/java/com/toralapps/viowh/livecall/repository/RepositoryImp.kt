package com.toralapps.viowh.livecall.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.toralapps.viowh.livecall.retrofit.Api
import com.toralapps.viowh.livecall.singletons.ListOfVideos
import com.toralapps.viowh.livecall.videolistmodel.ReportModel
import com.toralapps.viowh.livecall.videolistmodel.VideoList
import javax.inject.Inject

class RepositoryImp @Inject constructor(
    private val api: Api,
    private val cachVideoList: DataStore<VideoList>
){

    val response=MutableLiveData<Response<VideoList>>()
    val liveDataVideo:LiveData<Response<VideoList>> get() = response

    val cachVideoListStrem = cachVideoList.data



    suspend fun getVideos() {
        response.value = Response.Loading()

        var result:retrofit2.Response<VideoList>? = null
        try {
            if (ListOfVideos.Store?.body() == null) {
                result = api.getVideo(ListOfVideos.selectedApi)
                ListOfVideos.Store =result
            }
            else{
                result = ListOfVideos.Store
            }

       if(result!!.isSuccessful && result.body()!= null && result.body()!!.Data.isNotEmpty()){
           val respones = result.body() ?: VideoList()
           response.value = Response.Success(respones)
           updateCacheVideoList(respones)

       }else{
          errorInNetwrokCall()
       }

    }catch (e:Exception){
            Log.d("ERROR in CALLING", e.printStackTrace().toString())
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

    private suspend fun updateCacheVideoList(respones: VideoList){
        cachVideoList.updateData {
            it.copy(
                Data = respones.Data,
                Settings = respones.Settings,
                HttpStatus = respones.HttpStatus,
                Message = respones.Message,
                Status = respones.Status
            )
        }
    }

    private suspend fun errorInNetwrokCall(){
        if (ListOfVideos.selectedIndex == ListOfVideos.ListOfApis.size){
            cachVideoListStrem.collect{
                if (it.Data.isEmpty()){
                    response.value = Response.error("Please try after sometimes")
                }else{
                    response.value = Response.Success(it)
                    Log.d("CACHESTREM", "getVideos: flow collected")
                }
            }
        }else{
            Log.d("RECALLING", "getVideos: ")
            response.value = Response.error("Please try after sometimes")
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