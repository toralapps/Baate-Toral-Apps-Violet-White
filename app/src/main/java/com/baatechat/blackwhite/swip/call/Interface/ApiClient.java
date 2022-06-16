package com.baatechat.blackwhite.swip.call.Interface;

import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiClient {

    /*@GET("com.printf.demo.json")
    fun GetAd():Call<AdModelResponse>*/
//    @GET("pink-white-video-chat.json")
    @GET("printf-test-ad.json")
    Call<Root> GetAd();
}
