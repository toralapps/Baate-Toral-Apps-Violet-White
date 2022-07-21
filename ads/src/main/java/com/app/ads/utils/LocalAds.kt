package com.app.ads.utils

import com.app.ads.domain.models.AdsRoot
import retrofit2.Response

object LocalAds {

    var AdsStore: Response<AdsRoot>? = null

}