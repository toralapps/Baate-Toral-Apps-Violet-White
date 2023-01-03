package com.app.ads.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.app.ads.data.remote.AdsApiCall
import com.app.ads.domain.models.AdsRoot
import com.app.ads.domain.repository.AdsRepository
import com.app.ads.utils.LocalAds
import com.app.ads.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AdsRepositoryImp @Inject constructor(
    val adsApiCall:AdsApiCall,
):AdsRepository {

    override suspend fun getAddInfo(): Flow<Response<AdsRoot>> {
        return flow<Response<AdsRoot>> {
            var result: retrofit2.Response<AdsRoot>? = null
            try {
                if (LocalAds.AdsStore == null) {
                    Log.d("Adsapi", "Calling api")
                    result = adsApiCall.getAds(LocalAds.selectedApi)
                    LocalAds.AdsStore = result
                } else {
                    Log.d("Adsapi", "passing sotre value")
                    result = LocalAds.AdsStore
                }
            } catch (e: Exception) {
                emit(Response.error("An Exception occurs"))
            }
            if (result?.isSuccessful == true && result.body()?.data?.ads?.isNotEmpty() == true) {
                emit(Response.Success(result.body()))
            } else {
                emit(Response.error(result?.errorBody().toString()))
            }
        }
    }

}