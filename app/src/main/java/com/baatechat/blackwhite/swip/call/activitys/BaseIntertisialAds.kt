package com.baatechat.blackwhite.swip.call.activitys

import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.baatechat.blackwhite.swip.call.Interface.*
import com.baatechat.blackwhite.swip.call.saveApiCall
import com.facebook.ads.Ad
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAdListener

import com.google.ads.mediation.facebook.FacebookMediationAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSourceBannerLayout
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.InterstitialListener


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
open class BaseInterstialAd : AppCompatActivity() {

    var mAdView: AdView? = null
    var adDataObject: Root? = null
    var bannerLayout: IronSourceBannerLayout? = null
    var madView: com.facebook.ads.AdView? = null


    companion object {
        var ironSource_App_id = ""
        var admob_bannerkey = ""
        var admob_interstitial_key = ""
        var admob_native_key = ""
        var admob_App_id: String? = null
        var admob_rewareded_key = ""
        var ironSource_banner_key = ""
        var isAddShowing = false
        var showAddcounter = true
        var ironSource_interstitial_key = ""
        var fbInterstitialAd: com.facebook.ads.InterstitialAd? = null
        var mInterstitialAd: InterstitialAd? = null
        var ironSource_native_key = ""
        var ironSource_rewareded_key = ""
        var ironSource: IronSource? = null
        var firstMonetizationId = 0
        var fb_App_id =""
        var fb_bannerkey= ""
        var fb_interstitialkey = ""
        var fb_nativekey  = ""
        var fb_rewarededkey = ""
    }
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)
    }

    fun initLoad() {
        if (firstMonetizationId == 1) {
            Log.d("ADTEST","facbook initload called")
            loadFacebookInterstialAd()
        }
        else if (firstMonetizationId == 2) //For Facebook Monetization
        {
            Log.d("ADTEST","ADMOB initload called")
            loadAdMobInterstitialAd()
        } else if (firstMonetizationId == 6) //For AdMob
        {
            Log.d("ADTEST","Ironsource initload called")
            loadIronSourceInterstialAd()
        }

    }


    fun initShow() {
        if (firstMonetizationId == 1) {
            Log.d("ADTEST","facbook initshow called")
            showFacebookinitstiaalAdView()
        }
        else if (firstMonetizationId == 2) {
            Log.d("ADTEST","ADmob initshow called")
            showAdMobInterstialAd()
        } else if (firstMonetizationId == 6) {
            Log.d("ADTEST","IronSource initshow called")
            showIronSourceInterstitialAd()
        }
    }
    fun loadFacebookInterstialAd()
    {
        fbInterstitialAd = com.facebook.ads.InterstitialAd(this, fb_interstitialkey)
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
                Log.d("DEEP","FacBook Add Showed")
            }

            override fun onInterstitialDismissed(ad: Ad) {
            }

            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("ADTEST","failed facebook  interstialAd")
                loadIronSourceInterstialAd()
                //   showAdMobInterstialAd()
            }

            override fun onAdLoaded(ad: Ad) {
                Log.d("ADTEST","facbook ADLoaded")
                isAddShowing = true
                // Interstitial ad is loaded and ready to be displayed
                // Show the ad
                // fbInterstitialAd!!.show()

            }

            override fun onAdClicked(ad: Ad) {}
            override fun onLoggingImpression(ad: Ad) {

            }
        }
        fbInterstitialAd!!.loadAd(
            fbInterstitialAd!!.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build()
        )

    }
    fun showFacebookinitstiaalAdView()
    {

            fbInterstitialAd?.let {
            try{
                it.show()
                Log.d("DEEP","facebook add showe")
            }catch (e:Exception){
                Log.d("DEEP","facBook add excaption")
                showAdMobInterstialAd()
            }
        }
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
                Log.d("ADTEST","facbook Displayed show method")
                isAddShowing = false
                showAddcounter = false
            }

            override fun onInterstitialDismissed(ad: Ad) {
                isAddShowing = false
                showAddcounter = false
            }

            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("ADTEST","failed facebook  interstialAd show method")
                showIronSourceInterstitialAd()
                //   showAdMobInterstialAd()
            }

            override fun onAdLoaded(ad: Ad) {
                Log.d("ADTEST","facbook ADLoaded showed method")
                // Interstitial ad is loaded and ready to be displayed
                // Show the ad
                // fbInterstitialAd!!.show()

            }

            override fun onAdClicked(ad: Ad) {}
            override fun onLoggingImpression(ad: Ad) {

            }
        }
        fbInterstitialAd?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)
    }
    fun loadIronSourceInterstialAd() {

        if (isAddShowing) {
            Log.d("DEEP","return to call from ironsource")
            return
        }
        IronSource.init(this, ironSource_App_id)

        IronSource.loadInterstitial()
        IronSource.setInterstitialListener(object : InterstitialListener {
            override fun onInterstitialAdReady() {
                Log.d("DEEP","IRONSSOURCE ADD READY")
                isAddShowing = true
            }
            override fun onInterstitialAdLoadFailed(ironSourceError: IronSourceError) {
                Log.d("ADTEST","Ironsource ADLoaded failed")
                loadAdMobInterstitialAd()
            }

            override fun onInterstitialAdOpened() {}
            override fun onInterstitialAdClosed() {
                IronSource.loadInterstitial()
            }

            override fun onInterstitialAdShowSucceeded() {}
            override fun onInterstitialAdShowFailed(ironSourceError: IronSourceError) {}
            override fun onInterstitialAdClicked() {}
        })
    }

    fun showIronSourceInterstitialAd() {
        IronSource.init(this, ironSource_App_id)

        IronSource.showInterstitial()
        IronSource.setInterstitialListener(object : InterstitialListener {
            override fun onInterstitialAdReady() {
                Log.d("ADTEST","Ironsource showed")
            }
            override fun onInterstitialAdLoadFailed(ironSourceError: IronSourceError) {
                Log.d("ADTEST","ironsource ADLoaded failed")
                showAdMobInterstialAd()
            }

            override fun onInterstitialAdOpened() {}
            override fun onInterstitialAdClosed() {
                isAddShowing = false
                showAddcounter = false
                IronSource.loadInterstitial()
            }

            override fun onInterstitialAdShowSucceeded() {
                showAddcounter = false
            }
            override fun onInterstitialAdShowFailed(ironSourceError: IronSourceError) {
                isAddShowing = false
                showAdMobInterstialAd()
                Log.d("DEEP","IRONSURCE ADD SHOWING ERROR ${ironSourceError.errorMessage}")
            }
            override fun onInterstitialAdClicked() {}
        })
    }

    fun loadAdMobInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this@BaseInterstialAd, admob_interstitial_key, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    isAddShowing = true
                    mInterstitialAd = interstitialAd
                    Log.d("ADTEST","ADMOB ADLoaded")
                    Log.i(FacebookMediationAdapter.TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.i(FacebookMediationAdapter.TAG, loadAdError.message)
                    mInterstitialAd = null
                    Log.d("ADTEST","ADMOB ADLoaded Failed")
                    loadFacebookInterstialAd()
                }
            })
    }

    fun showAdMobInterstialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(this@BaseInterstialAd)
            mInterstitialAd!!.setFullScreenContentCallback(object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("TAG", "The ad was dismissed.")
                    val adRequest = AdRequest.Builder().build()
                    InterstitialAd.load(this@BaseInterstialAd,
                        admob_interstitial_key,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                mInterstitialAd = interstitialAd
                                //
                                //                                    showad(view);

                                Log.i(FacebookMediationAdapter.TAG, "onAdLoaded")


                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                // Handle the error
                                Log.i(FacebookMediationAdapter.TAG, loadAdError.message)
                                mInterstitialAd = null
                                Log.d("ADTEST","ADMOB ADLoaded FAILED show method")
                                showFacebookinitstiaalAdView()
                            }
                        })
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("TAG", "The ad failed to show.")
                }

                override fun onAdShowedFullScreenContent() {
                    mInterstitialAd = null
                    isAddShowing = false
                    showAddcounter = false
                    Log.d("TAG", "The ad was shown.")
                }
            })
        } else {
            showFacebookinitstiaalAdView()
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }
    open fun getAdDataApi() {
        if (!ironSource_App_id.isEmpty() || !admob_bannerkey.isEmpty() || !admob_native_key.isEmpty() || !admob_interstitial_key.isEmpty() || !admob_rewareded_key.isEmpty() || !ironSource_App_id.isEmpty()) {
            if (ironSource_App_id == null && admob_bannerkey == null && admob_interstitial_key == null && admob_native_key == null && admob_rewareded_key == null) {
                //  progressDoalog.dismiss();
                Log.e("Inside", " api call initLoad gone")
            } else {
                Log.e("Inside", "Skip api call initLoad")
                initLoad()
            }
            return
        }
        val client = ServiceGenerator.createService(
            ApiClient::class.java, this)
        try {
            val call = client.GetAd()
            call.enqueue(object : Callback<Root?> {
                override fun onResponse(call: Call<Root?>, response: Response<Root?>) {
                    if (response.isSuccessful) {
                        Log.d("DEEP", "Ads from api call")
                        Log.e("Data", "Successfull")
                        adDataObject = response.body()
                        val list = java.util.ArrayList<Data>()
                        if (adDataObject!!.getData() != null) {
                            if (adDataObject!!.getData()
                                    .getAds() != null && !adDataObject!!.getData()
                                    .getAds().isEmpty()
                            ) {
                                list.add(adDataObject!!.getData())
                                saveApiCall.saveApi(this@BaseInterstialAd, list)
                            }
                            if (adDataObject!!.getData().getShowAds()) {
                                var i = 0
                                if (adDataObject != null && !adDataObject!!.getData()
                                        .getAds().isEmpty()
                                ) {
                                    //facebook
                                    i = 0
                                    while (i < adDataObject!!.getData().getAds().size) {
                                        val it: Ads =
                                            adDataObject!!.getData().getAds().get(i)

                                        if (it.monetizationId == 1) { // For Facebook Monetization
                                            if (firstMonetizationId == 0) {
                                                firstMonetizationId = it.monetizationId
                                            }
                                            fb_App_id = it.adAppKey
                                            fb_bannerkey = it.bannerAdKey
                                            fb_interstitialkey = it.interstitialAdKey
                                            fb_rewarededkey = it.rewardedAdKey
                                            fb_nativekey = it.nativeAdKey
                                        }
                                        else if (it.monetizationId == 2) { // For Facebook Monetization
                                            if (firstMonetizationId == 0) {
                                                firstMonetizationId =
                                                    it.monetizationId
                                            }
                                            admob_App_id = it.adAppKey
                                            admob_bannerkey = it.bannerAdKey
                                            admob_interstitial_key =
                                                it.interstitialAdKey
                                            admob_rewareded_key = it.rewardedAdKey
                                        } else if (it.monetizationId == 6) { // For AdMob
                                            if (firstMonetizationId == 0) {
                                                firstMonetizationId =
                                                    it.monetizationId
                                            }
                                            ironSource_App_id = it.adAppKey
                                            ironSource_banner_key = it.bannerAdKey
                                            ironSource_interstitial_key =
                                                it.interstitialAdKey
                                            ironSource_rewareded_key =
                                                it.rewardedAdKey
                                            ironSource_native_key = it.nativeAdKey
                                        } else {
                                            break
                                        }

                                        i++
                                    }
                                }
                                if (admob_bannerkey == null && admob_interstitial_key == null && admob_native_key == null && admob_rewareded_key == null) {

                                } else {
                                    Log.e("Inside", "api call initLoad")
                                    initLoad()
                                }
                            } else {

                            }
                        } else {
                            apiFromSharedPrefrences()
                        }
                    } else {
                        Log.d("DEEP", "API call is not successful")
                        apiFromSharedPrefrences()
                    }
                }

                override fun onFailure(call: Call<Root?>, t: Throwable) {
                    Log.d("DEEP", "ads api call is failed")
                    apiFromSharedPrefrences()
                    // Toast.makeText(BaseActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            })
        } catch (e: Exception) {
            Log.d("DEEP", " ads catch block is called in ")
            apiFromSharedPrefrences()
        }
    }

    open fun apiFromSharedPrefrences() {
        var storedAds = ArrayList<Data?>()
        Log.d("DEEP", "failed in api call")
        Log.d("DEEP", "Ads from Sharedprefrences")
        storedAds = saveApiCall.getAds(this@BaseInterstialAd)
        if (!storedAds.isEmpty() && isNetworkAvailable()) {
            Log.d(
                "DEEP",
                "Internate is connected " + isNetworkAvailable() + "and store ads is not empty"
            )
            val data = storedAds[0]
            if (data != null) {
                if (data.showAds) {
                    var i = 0
                    if (data != null && !data.ads.isEmpty()) {
                        //facebook
                        i = 0
                        while (i < data.ads.size) {
                            val it = data.ads[i]
                            if (it.monetizationId == 1) { // For Facebook Monetization
                                if (firstMonetizationId == 0) {
                                    firstMonetizationId = it.monetizationId
                                }
                                fb_App_id = it.adAppKey
                                fb_bannerkey = it.bannerAdKey
                                fb_interstitialkey = it.interstitialAdKey
                                fb_rewarededkey = it.rewardedAdKey
                                fb_nativekey = it.nativeAdKey
                            }
                            else  if (it.monetizationId == 2) { // For Facebook Monetization
                                if (firstMonetizationId == 0) {
                                    firstMonetizationId = it.monetizationId
                                }
                                admob_App_id = it.adAppKey
                                admob_bannerkey = it.bannerAdKey
                                admob_interstitial_key = it.interstitialAdKey
                                admob_rewareded_key = it.rewardedAdKey
                                admob_native_key = it.nativeAdKey
                            } else if (it.monetizationId == 6) { // For AdMob
                                if (firstMonetizationId == 0) {
                                    firstMonetizationId = it.monetizationId
                                }
                                ironSource_App_id = it.adAppKey
                                ironSource_banner_key = it.bannerAdKey
                                ironSource_interstitial_key = it.interstitialAdKey
                                ironSource_rewareded_key = it.rewardedAdKey
                                ironSource_native_key = it.nativeAdKey
                            } else {
                                break
                            }

                            i++
                        }
                    }
                    if (admob_bannerkey == null && admob_interstitial_key == null && admob_native_key == null && admob_rewareded_key == null) {
                        Log.d("ID", "Keys are empty")
                    } else {
                        Log.d("ID", "api call initLoad")
                        initLoad()
                    }
                } else {
                }
            }
        }
    }

    open fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    override fun onResume() {
        super.onResume()
        Log.d("DEEP","Resume")
        IronSource.onResume(this)
        getAdDataApi()
    }

    override fun onPause() {
        super.onPause()
        Log.d("DEEP","pause")
        IronSource.onPause(this)
    }

    override fun onDestroy() {
        Log.d("DEEP","destroyed")
        showAddcounter = true
        IronSource.destroyBanner(bannerLayout)
        super.onDestroy()
    }
}

