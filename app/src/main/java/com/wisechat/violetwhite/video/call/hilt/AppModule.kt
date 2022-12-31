package com.wisechat.violetwhite.video.call.hilt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.app.ads.CachVideos
import com.app.ads.protoserializer.CachVideoSerializer
import com.wisechat.violetwhite.video.call.protodatastore.VIdeolistSerializer
import com.wisechat.violetwhite.video.call.retrofit.Api
import com.wisechat.violetwhite.video.call.videolistmodel.VideoList
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

    @Provides
    @Singleton
    fun provideVideoListDataStore(
        @ApplicationContext context: Context,
        vIdeolistSerializer: VIdeolistSerializer
    ): DataStore<VideoList> =
        DataStoreFactory.create(
            serializer = vIdeolistSerializer,
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        ){
            context.dataStoreFile("video_list.pb")
        }
}