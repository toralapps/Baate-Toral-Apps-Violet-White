package com.nimychat.skybluewhite.videocall.chat.hilt

import com.nimychat.skybluewhite.videocall.chat.retrofit.Api
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
            .baseUrl("https://printf-app-data.github.io/Video-Chat-Test-API/")
           // .baseUrl("https://raw.githubusercontent.com/appsplayerstudio/appsplayerstudio.github.io/main/Data/baate-red-pink-chat/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRestaurantApi(retrofit: Retrofit): Api =
        retrofit.create(Api::class.java)

}