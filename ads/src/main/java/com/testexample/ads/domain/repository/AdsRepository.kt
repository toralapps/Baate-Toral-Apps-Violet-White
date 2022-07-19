package com.testexample.ads.domain.repository

import com.testexample.ads.domain.models.AdsRoot
import com.testexample.ads.utils.Response
import kotlinx.coroutines.flow.Flow

interface AdsRepository {

    suspend fun getAddInfo():Flow<Response<AdsRoot>>
}