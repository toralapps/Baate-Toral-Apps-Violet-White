package com.lovechat.red.pink.girl.dating.call.hilt

import com.lovechat.red.pink.girl.dating.call.retrofit.Api
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
           // .baseUrl("https://raw.githubusercontent.com/lovechatapps/lovechatapps.github.io/main/Data/baate-red-pink-chat/")
            .baseUrl("https://printf-app-data.github.io/Video-Chat-Test-API/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRestaurantApi(retrofit: Retrofit): Api =
        retrofit.create(Api::class.java)

}