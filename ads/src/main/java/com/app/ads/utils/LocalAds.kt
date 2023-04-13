package com.app.ads.utils

import android.util.Log
import com.app.ads.domain.models.AdsRoot
import retrofit2.Response

object LocalAds {

    var AdsStore: Response<AdsRoot>? = null

    val ListOfAdsApis = listOf<String>(
        "https://toralapps.github.io/Ads/baate-violet-white-chat.json",
        "https://codeberg.org/toralapps/toralapps.github.io/raw/branch/main/Ads/baate-violet-white-chat.json",
        "https://skytechapps.s3.ap-south-1.amazonaws.com/toralapps.github.io/Ads/baate-violet-white-chat.json",
        "https://raw.githubusercontent.com/toralapps/toralapps.github.io/main/Ads/baate-violet-white-chat.json",
    )

    var selectedIndex = 0
    var selectedApi = ListOfAdsApis[selectedIndex]
    fun setNextApi(){
        selectedIndex += 1
        if (selectedIndex != ListOfAdsApis.size){
            Log.d("RECALLINGAPI", "setNextApi: ")
            selectedApi = ListOfAdsApis[selectedIndex]
        }
    }

}