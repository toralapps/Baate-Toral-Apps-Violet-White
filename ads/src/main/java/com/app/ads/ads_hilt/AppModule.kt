package com.app.ads.ads_hilt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.app.ads.AdsJavaViewModel
import com.app.ads.AdsViewModel
import com.app.ads.CachVideos
import com.app.ads.data.remote.AdsApiCall
import com.app.ads.protoserializer.CachVideoSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
            .baseUrl("https://raw.githubusercontent.com/appsplayerstudio/appsplayerstudio.github.io/main/Ads/")
//           .baseUrl("https://raw.githubusercontent.com/printf-app-data/printf-app-data.github.io/main/Ad-Test-API/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

//    @Provides
//    @Singleton
//    fun provideCachVideoes(
//        @ApplicationContext context: Context,
//        cachVideoSerializer: CachVideoSerializer
//    ):DataStore<CachVideos> =
//        DataStoreFactory.create(
//            serializer = cachVideoSerializer,
//            corruptionHandler = null,
//            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
//        ){
//            context.dataStoreFile("cache_videos.pb")
//        }

}