package com.testexample.ads.data.remote

import com.testexample.ads.domain.models.AdsRoot
import retrofit2.Response
import retrofit2.http.GET

interface AdsApiCall {

    @GET("test-ads-true.json")
    suspend fun getAds():Response<AdsRoot>

}