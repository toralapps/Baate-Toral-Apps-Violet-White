package com.testexample.ads.utils

import com.testexample.ads.domain.models.AdsRoot
import retrofit2.Response

object LocalAds {

    var AdsStore: Response<AdsRoot>? = null

}