package com.app.ads.utils

import android.util.Log
import com.app.ads.domain.models.AdsRoot
import retrofit2.Response

object LocalAds {

    var AdsStore: Response<AdsRoot>? = null

    val ListOfAdsApis = listOf<String>(
        "https://raw.githubusercontent.com/appsplayerstudio/appsplayerstudio.github.io/main/Ads/baate-red-pink-chat.jsond",
        "https://codeberg.org/printf-app-data/pages/raw/branch/main/Video-Chat-Test-API/getimageslist-live.json",
        "https://codeberg.org/printf-app-data/pages/raw/branch/main/Video-Chat-Test-API/save.json",
        "https://codeberg.org/printf-app-data/pages/raw/branch/main/Ad-Test-API/test-ads-true.json",
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