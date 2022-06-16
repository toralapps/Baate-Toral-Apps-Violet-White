package com.baatechat.blackwhite.swip.call.activitys

import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.baatechat.blackwhite.swip.call.Interface.*
import com.baatechat.blackwhite.swip.call.saveApiCall
import com.facebook.ads.Ad
import com.facebook.ads.AdSize.BANNER_HEIGHT_50
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAdListener

import com.google.ads.mediation.facebook.FacebookMediationAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSourceBannerLayout
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.BannerListener
import com.ironsource.mediationsdk.sdk.InterstitialListener


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseClass : AppCompatActivity() {


    var mAdView: AdView? = null
    var adContainer: LinearLayout? = null
    var bannerLayout: IronSourceBannerLayout? = null
    var madView: com.facebook.ads.AdView? = null

    companion object {
        var ironSource_App_id = ""
        var ironSource: IronSource? = null
        var firstMonetizationId = 0
        var admob_bannerkey = ""
        var adDataObject: Root? = null
        var mInterstitialAd: InterstitialAd? = null
        var fbInterstitialAd: com.facebook.ads.InterstitialAd? = null
        var admob_interstitial_key = ""
        var admob_native_key = ""
        var admob_App_id: String? = null
        var admob_rewareded_key = ""
        var ironSource_banner_key = ""
        var ironSource_interstitial_key = ""
        var ironSource_native_key = ""
        var ironSource_rewareded_key = ""
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
        Log.d("DEEP","initLoad called")
        if (firstMonetizationId == 1) //For Facebook Monetization
        {
            Log.d("DEEP","facbook init called")
            loadBannerFaceBoook()
            loadFacebookInterstialAd()
        }
        else if (firstMonetizationId == 2) //For Facebook Monetization
        {
            Log.d("DEEP","admob init called")
            loadAdMobBannerAd()
            loadAdMobInterstitialAd()
        } else if (firstMonetizationId == 6) //For AdMob
        {
            Log.d("DEEP","IronSource init called")
            loadIronSourceBannerAd()
            loadIronSourceInterstialAd()
        }

    }

    fun initShow() {
        if (firstMonetizationId == 1) {
            showFacebookinitstiaalAdView()
        }
        else if (firstMonetizationId == 2) {
            showAdMobInterstialAd()
        } else if (firstMonetizationId == 6) {
            showIronSourceInterstitialAd()
        }
    }

    fun loadFacebookInterstialAd()
    {
        fbInterstitialAd = com.facebook.ads.InterstitialAd(this, fb_interstitialkey)
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
            }

            override fun onInterstitialDismissed(ad: Ad) {
            }

            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("DEEP","failed facebook  interstialAd")
                loadAdMobInterstitialAd()
                //   showAdMobInterstialAd()
            }

            override fun onAdLoaded(ad: Ad) {
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
            }

            override fun onInterstitialDismissed(ad: Ad) {
            }

            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("DEEP","failed facebook  interstialAd")
                showAdMobInterstialAd()
                //   showAdMobInterstialAd()
            }

            override fun onAdLoaded(ad: Ad) {
                // Interstitial ad is loaded and ready to be displayed
                // Show the ad
                // fbInterstitialAd!!.show()

            }

            override fun onAdClicked(ad: Ad) {}
            override fun onLoggingImpression(ad: Ad) {

            }
        }
        fbInterstitialAd!!.buildLoadAdConfig().withAdListener(interstitialAdListener)
    }
    open fun loadBannerFaceBoook() {
        if (adContainer!!.childCount > 0) {
            adContainer!!.removeAllViews()

        }


        val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {


            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("DEEP","Facbook banner add failed")
                loadAdMobBannerAd()
            }

            override fun onAdLoaded(p0: Ad?) {
                Log.d("DEEP","FACBOOK BANNER ADD LOAD")
            }

            override fun onAdClicked(p0: Ad?) {
            }

            override fun onLoggingImpression(p0: Ad?) {
            }


        }
        madView = com.facebook.ads.AdView(this, fb_bannerkey, BANNER_HEIGHT_50)

        adContainer!!.addView(madView)

        // madView!!.loadAd()
        madView!!.loadAd(madView!!.buildLoadAdConfig().withAdListener(adListener).build());


    }

    /*  open fun showFacebookinitstiaalAdView() {

          fbInterstitialAd = com.facebook.ads.InterstitialAd(this, fb_interstitialkey)
          val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
              override fun onInterstitialDisplayed(ad: Ad) {
                  // Interstitial ad displayed callback
              }

              override fun onInterstitialDismissed(ad: Ad) {
              }

              override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                  Log.d("DEEP","failed facebook  interstialAd")
                  loadAdMobInterstitialAd()
                  showAdMobInterstialAd()
              }

              override fun onAdLoaded(ad: Ad) {
                  // Interstitial ad is loaded and ready to be displayed
                  // Show the ad
                  fbInterstitialAd!!.show()

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
      }*/
    fun loadIronSourceBannerAd() {
        if (adContainer!!.childCount > 0) {
            adContainer!!.removeAllViews()
        }
        IronSource.init(this, ironSource_App_id)
        bannerLayout?.let{
            Log.d("DEEP","bannerlayout is not null & destroyed")
            IronSource.destroyBanner(it)
        }
        Log.d("DEEP","banner init")
        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE")
        bannerLayout = IronSource.createBanner(this, ISBannerSize.BANNER)
        Log.d("DEEP","Banner created")
        adContainer!!.addView(bannerLayout)
        bannerLayout!!.bannerListener = object : BannerListener {
            override fun onBannerAdLoaded() {
                Log.d("DEEP","iron surce bannerloaded")
            }
            override fun onBannerAdLoadFailed(ironSourceError: IronSourceError) {
                Log.d("DEEP","iron source add failed")
                loadAdMobBannerAd()
            }

            override fun onBannerAdClicked() {}
            override fun onBannerAdScreenPresented() {}
            override fun onBannerAdScreenDismissed() {}
            override fun onBannerAdLeftApplication() {}
        }
        IronSource.loadBanner(bannerLayout)

        //MediationTestSuite.launch(BaseActivity.this);
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

    fun loadAdMobBannerAd() {
        if (adContainer!!.childCount > 0) {
            adContainer!!.removeAllViews()
        }
        val adRequest = AdRequest.Builder().build()
        mAdView = AdView(applicationContext)
        mAdView!!.adSize = AdSize.BANNER
        mAdView!!.adUnitId = admob_bannerkey
        mAdView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("DEEP","banner AdMob add load")
            }
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.d("DEEP","banner Admob add load failed")
                loadIronSourceBannerAd()
                //  showBannerFaceBoook();
            }
        }
        adContainer!!.addView(mAdView)
        mAdView!!.loadAd(adRequest)
        // MediationTestSuite.launch(BaseActivity.this);
    }
    fun loadAdMobInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this@BaseClass, admob_interstitial_key, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.d("DEEP","Admob interstitalAd add load")
                    Log.i(FacebookMediationAdapter.TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.d("DEEP","Admob interstitalAd add load Failed")
                    Log.i(FacebookMediationAdapter.TAG, loadAdError.message)
                    mInterstitialAd = null
                    loadIronSourceInterstialAd()
                }
            })
    }

    fun showAdMobInterstialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(this@BaseClass)
            mInterstitialAd!!.setFullScreenContentCallback(object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("TAG", "The ad was dismissed.")
                    val adRequest = AdRequest.Builder().build()
                    InterstitialAd.load(this@BaseClass,
                        admob_interstitial_key,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                Log.d("DEEP","Admob interstitalAd add load Failed second")
                                mInterstitialAd = interstitialAd
                                //
                                //                                    showad(view);


                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                // Handle the error
                                Log.i(FacebookMediationAdapter.TAG, loadAdError.message)
                                Log.d("DEEP","Admob interstitalAd add load Failed second")
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
                    Log.d("DEEP","Admob interstitalAd add showing")
                    Log.d("TAG", "The ad was shown.")
                }
            })
        } else {
            showIronSourceInterstitialAd()
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

    open fun getAdDataApi() {
        if (!ironSource_App_id.isEmpty() || !admob_bannerkey.isEmpty() || !admob_native_key.isEmpty() || !admob_interstitial_key.isEmpty() || !admob_rewareded_key.isEmpty() || !ironSource_App_id.isEmpty() || !fb_App_id.isEmpty()
            || !fb_bannerkey.isEmpty() || !fb_interstitialkey.isEmpty() || !fb_rewarededkey.isEmpty() || !fb_nativekey.isEmpty()) {
            if (ironSource_App_id == null && admob_bannerkey == null && admob_interstitial_key == null && admob_native_key == null && admob_rewareded_key == null
                && fb_App_id == null && fb_bannerkey == null && fb_interstitialkey == null && fb_rewarededkey == null && fb_nativekey == null) {
                adContainer!!.visibility = View.GONE
                //  progressDoalog.dismiss();
                Log.d("DEEP", " api call initLoad gone")
            } else {
                Log.d("DEEP", "Skip api call initLoad")
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
                                saveApiCall.saveApi(this@BaseClass, list)
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
                                    adContainer!!.visibility = View.GONE
                                } else {
                                    Log.e("Inside", "api call initLoad")
                                    initLoad()
                                }
                            } else {
                                adContainer!!.visibility = View.GONE
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
        storedAds = saveApiCall.getAds(this@BaseClass)
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

                            /*admob_AppId = it.getAdAppKey();
                                admob_bannerkey = it.getBannerAdKey();
                                admob_interstitialkey = it.getInterstitialAdKey();
                                admob_rewarededkey = it.getRewardedAdKey();
                                admob_nativekey = it.getNativeAdKey();
                                break;*/
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
                        adContainer!!.visibility = View.GONE
                        Log.d("ID", "Keys are empty")
                    } else {
                        Log.d("ID", "api call initLoad")
                        initLoad()
                    }
                } else {
                    adContainer!!.visibility = View.GONE
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
        Log.d("DEEP","resume")
        IronSource.onResume(this)
        getAdDataApi()
    }

    override fun onPause() {
        super.onPause()
        Log.d("DEEP","pause")
        bannerLayout?.let {
            IronSource.destroyBanner(it)
        }
    }

    override fun onDestroy() {
        Log.d("DEEP","destroy")
        IronSource.destroyBanner(bannerLayout)
        super.onDestroy()
    }

}

