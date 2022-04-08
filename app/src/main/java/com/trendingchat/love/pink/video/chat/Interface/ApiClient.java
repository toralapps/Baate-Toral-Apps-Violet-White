package com.trendingchat.love.pink.video.chat.Interface;

import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiClient {

    /*@GET("com.printf.demo.json")
    fun GetAd():Call<AdModelResponse>*/
    @GET("lets-meet-video-chat.json")
    Call<Root> GetAd();
}
