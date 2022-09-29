package com.toopchart.maroon.blue.love.video.call.hilt

import com.toopchart.maroon.blue.love.video.call.retrofit.Api
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/toopchartchat/toopchartchat.github.io/main/Data/baate-maroon-blue-chat/")
//            .baseUrl("https://printf-app-data.github.io/Video-Chat-Test-API/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRestaurantApi(retrofit: Retrofit): Api =
        retrofit.create(Api::class.java)

}