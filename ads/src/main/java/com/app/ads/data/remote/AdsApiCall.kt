package com.app.ads.data.remote

import com.app.ads.domain.models.AdsRoot
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface AdsApiCall {


//    @GET("test-ads-true.json")
    @GET
    suspend fun getAds(@Url adsurl:String):Response<AdsRoot>

}