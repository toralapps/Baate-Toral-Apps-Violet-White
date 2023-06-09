package com.toralapps.viowh.livecall.hilt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.toralapps.viowh.livecall.protodatastore.VIdeolistSerializer
import com.toralapps.viowh.livecall.retrofit.Api
import com.toralapps.viowh.livecall.videolistmodel.VideoList
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
            .baseUrl("https://toralapps.github.io/Data/baate-violet-white-chat/")
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