package com.app.ads.ads_hilt

import com.app.ads.data.repository.AdsRepositoryImp
import com.app.ads.domain.repository.AdsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAdsRepository(adsRepository: AdsRepositoryImp):AdsRepository

}