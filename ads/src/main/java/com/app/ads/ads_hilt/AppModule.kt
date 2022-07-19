package com.app.ads.ads_hilt

import com.app.ads.data.remote.AdsApiCall
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getAdsApi():AdsApiCall{
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/lovechatapps/lovechatapps.github.io/main/Ads/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }


}