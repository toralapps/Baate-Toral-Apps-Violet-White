package com.app.ads.domain.repository

import com.app.ads.domain.models.AdsRoot
import com.app.ads.utils.Response
import kotlinx.coroutines.flow.Flow

interface AdsRepository {

    suspend fun getAddInfo():Flow<Response<AdsRoot>>
}