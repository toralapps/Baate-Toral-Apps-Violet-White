package com.videochat.letsmeetvideochat

import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import com.facebook.ads.AudienceNetworkAds

import com.google.ads.mediation.facebook.FacebookMediationAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSourceBannerLayout
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.InterstitialListener
import com.chaatyvideo.green.black.romantic.chat.Interface.*
import com.chaatyvideo.green.black.romantic.chat.saveApiCall


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
open class BaseIntertisialAds : AppCompatActivity() {

    companion object {
        var mAdView: AdView? = null
        var mInterstitialAd: InterstitialAd? = null
        var adDataObject: Root? = null
        var admob_bannerkey = ""
        var admob_interstitial_key = ""
        var admob_native_key = ""
        var admob_App_id: String? = null
        var admob_rewareded_key = ""
        var ironSource_banner_key = ""
        var ironSource_interstitial_key = ""
        var ironSource_native_key = ""
        var ironSource_rewareded_key = ""

        var ironSource_App_id = ""
        var ironSource: IronSource? = null
        var firstMonetizationId = 0
        var bannerLayout: IronSourceBannerLayout? = null

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
        if (firstMonetizationId == 2) //For Facebook Monetization
        {
            loadAdMobInterstitialAd()
        } else if (firstMonetizationId == 6) //For AdMob
        {
            loadIronSourceInterstialAd()
        }

        // LoadIronsourceBannerAD();
    }
  /*  fun initIntertistialAdLoad() {
        if (firstMonetizationId == 2) //For Facebook Monetization
        {

            loadAdMobInterstitialAd()
        } else if (firstMonetizationId == 6) //For AdMob
        {
            loadIronSourceInterstialAd()

        }

        // LoadIronsourceBannerAD();
    }*/

    fun initShow() {
        if (firstMonetizationId == 2) {
            showAdMobInterstialAd()
        } else if (firstMonetizationId == 6) {
            showIronSourceInterstitialAd()
        }
    }

    fun loadIronSourceInterstialAd() {
        IronSource.init(this, ironSource_App_id)
        IronSource.loadInterstitial()
        IronSource.setInterstitialListener(object : InterstitialListener {
            override fun onInterstitialAdReady() {
            }
            override fun onInterstitialAdLoadFailed(ironSourceError: IronSourceError) {
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
            override fun onInterstitialAdReady() {}
            override fun onInterstitialAdLoadFailed(ironSourceError: IronSourceError) {
                showAdMobInterstialAd()
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

    fun loadAdMobInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this@BaseIntertisialAds, admob_interstitial_key, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i(FacebookMediationAdapter.TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.i(FacebookMediationAdapter.TAG, loadAdError.message)
                    mInterstitialAd = null
                    loadIronSourceInterstialAd()
                }
            })
    }

    fun showAdMobInterstialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(this@BaseIntertisialAds)
            mInterstitialAd!!.setFullScreenContentCallback(object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("TAG", "The ad was dismissed.")
                    val adRequest = AdRequest.Builder().build()
                    InterstitialAd.load(this@BaseIntertisialAds,
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
                                showIronSourceInterstitialAd()
                            }
                        })
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("TAG", "The ad failed to show.")
                }

                override fun onAdShowedFullScreenContent() {
                    mInterstitialAd = null
                    Log.d("TAG", "The ad was shown.")
                }
            })
        } else {
            showIronSourceInterstitialAd()
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
        val client = ServiceGenerator.createService(ApiClient::class.java, this)
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
                                saveApiCall.saveApi(this@BaseIntertisialAds, list)
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
                                        if (!it.adAppKey.isEmpty()) {
                                            /*admob_AppId = it.getAdAppKey();
                                    admob_bannerkey = it.getBannerAdKey();
                                    admob_interstitialkey = it.getInterstitialAdKey();
                                    admob_rewarededkey = it.getRewardedAdKey();
                                    admob_nativekey = it.getNativeAdKey();
                                    break;*/
                                            if (it.monetizationId == 2) { // For Facebook Monetization
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
        storedAds = saveApiCall.getAds(this@BaseIntertisialAds)
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
                            if (!it.adAppKey.isEmpty()) {
                                /*admob_AppId = it.getAdAppKey();
                                    admob_bannerkey = it.getBannerAdKey();
                                    admob_interstitialkey = it.getInterstitialAdKey();
                                    admob_rewarededkey = it.getRewardedAdKey();
                                    admob_nativekey = it.getNativeAdKey();
                                    break;*/
                                if (it.monetizationId == 2) { // For Facebook Monetization
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

}

