package com.app.ads.data.remote

import com.app.ads.domain.models.AdsRoot
import retrofit2.Response
import retrofit2.http.GET

interface AdsApiCall {

//    @GET("baate-red-pink-chat.json")
    @GET("test-ads-true.json")
    suspend fun getAds():Response<AdsRoot>

}