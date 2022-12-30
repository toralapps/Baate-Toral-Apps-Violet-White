package com.wisechat.violetwhite.video.call.hilt

import com.wisechat.violetwhite.video.call.retrofit.Api
import com.wisechat.violetwhite.video.call.singletons.ListOfVideos
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
            .baseUrl("https://codeberg.org/printf-app-data/pages/raw/branch/main/")
           // .baseUrl("https://raw.githubusercontent.com/appsplayerstudio/appsplayerstudio.github.io/main/Data/baate-red-pink-chat/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRestaurantApi(retrofit: Retrofit): Api =
        retrofit.create(Api::class.java)

}