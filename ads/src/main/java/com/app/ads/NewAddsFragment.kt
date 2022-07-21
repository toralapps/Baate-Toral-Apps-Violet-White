package com.app.ads

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.facebook.ads.Ad
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
import com.app.ads.domain.models.AdsItem
import com.app.ads.domain.models.Data
import com.app.ads.utils.AdsEvent
import com.app.ads.utils.Response
import com.app.ads.utils.SavedAds
import kotlinx.coroutines.launch


abstract class NewAddsFragment:Fragment() {

    abstract val adContainer:LinearLayout?
    private val adsViewModel : AdsViewModel by viewModels()
    private var adsRoot:Data? = null
    var mAdView: AdView? = null
    var bannerLayout: IronSourceBannerLayout? = null
    var madView: com.facebook.ads.AdView? = null
    var fb_isloaded:Boolean=false
    var admob_isloaded:Boolean=false
    var ironsource_isloaded:Boolean=false
    var mInterstitialAd: InterstitialAd? = null
    var fbInterstitialAd: com.facebook.ads.InterstitialAd? = null


    var fabBookAds:AdsItem? = null
    var ironsourceAds:AdsItem? = null
    var admobAds:AdsItem? = null

    private val TAG = "DEEPADSLog"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DEEP","onViewCreated of Super class")
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                adsViewModel.adsFlow.collect{ response ->
                     when (response) {
                        is Response.Success -> {
                            adsRoot = response.videoList?.data
                            SavedAds.saveApi(requireContext(), adsRoot)
                            if(adsRoot?.showAds == true){
                                filterAdsNetworkId()
                                loadInteritialAds()
                                loadBannerAds()
                            }
                            Log.d("DEEP","response from ads")
                        }

                        is Response.error -> {
                            Log.d(TAG, response.errorMassage.toString())
                            adsRoot = SavedAds.getAds(requireContext())
                            if(adsRoot?.showAds == true){
                                filterAdsNetworkId()
                                loadInteritialAds()
                                loadBannerAds()
                            }
                            Log.d("DEEP","Ads From SharedPrefrences ${adsRoot?.ads.toString()}}")

                        }

                        is Response.Loading -> {

                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun filterAdsNetworkId(){
       ironsourceAds = adsRoot?.ads?.filter {
            it?.monetizationId == 6
        }?.let {
            if (it.isNotEmpty()) it.get(0) else null
       }

       fabBookAds = adsRoot?.ads?.filter {
            it?.monetizationId == 1
        }?.let {
            if (it.isNotEmpty()) it.get(0) else null
      }

        admobAds = adsRoot?.ads?.filter {
            it?.monetizationId == 2
        }?.let {
            if(it.isNotEmpty()) it.get(0) else null
        }

    }


    fun loadInteritialAds(){
        adsRoot?.let { adsRoot ->
            Log.d("deep","load intertital called")
            val firstMonitizationId = adsRoot.ads?.get(0)?.monetizationId

            if (firstMonitizationId == 1) //For Facebook Monetization
            {
                loadFacebookInterstialAd()
            }
            else if (firstMonitizationId == 2) //For AdMob
            {
                loadAdMobInterstitialAd()

            } else if (firstMonitizationId == 6) //For IronSoruce
            {
              loadIronSourceInterstialAd()

            }
        }
    }

    fun loadBannerAds(){
        adsRoot?.let { adsRoot ->
            adContainer?.let {
                val firstMonitizationId = adsRoot.ads?.get(0)?.monetizationId

                if (firstMonitizationId == 1) //For Facebook Monetization
                {
                    loadFaceBookBannerAdd()
                }
                else if (firstMonitizationId == 2) //For AdMob
                {
                    loadAdMobBannerAd()

                } else if (firstMonitizationId == 6) //For IronSoruce
                {
                    loadIronSourceBannerAd()
                }
            }
        }
    }


    fun showIntertisialAdd(){
        Log.d("DEEP","SHOW CALL")
       adsRoot?.let {adsRoot ->
           if (ironsource_isloaded) //For Facebook Monetization
           {
               Log.d("DEEP","IRONSOURCEAD SHOW CALL")
               showIronSourceInterstitialAd()
           } else if (fb_isloaded) //For AdMob
           {
               Log.d("DEEP","FACBOOK AD SHOW CALL")
               showFacebookinitstiaal()

           } else if (admob_isloaded) //For IronSoruce
           {
               Log.d("DEEP","ADMOBADD SHOW CALL")
               showAdMobInterstialAd()
           }
           else{
               adsViewModel.onAdEvent(AdsEvent.onAdClose)
           }
       } ?: adsViewModel.onAdEvent(AdsEvent.onAdClose)
    }


    private fun loadIronSourceInterstialAd() {
        try {
            if (ironsource_isloaded) {
                Log.d("DEEP","return to call from ironsource")
                return
            }
            Log.d("DEEP","CALLING LOADINTERSTITAL OF IRONSURCE")
            IronSource.init(requireActivity(), ironsourceAds?.adAppKey)
            IronSource.loadInterstitial()
            IronSource.setInterstitialListener(object : InterstitialListener {
                override fun onInterstitialAdReady() {
                    Log.d("DEEP","IRONSSOURCE ADD READY")
                    ironsource_isloaded =true
                }

                override fun onInterstitialAdLoadFailed(ironSourceError: IronSourceError) {
                    loadAdMobInterstitialAd()
                }

                override fun onInterstitialAdOpened() {

                }
                override fun onInterstitialAdClosed() {
                    IronSource.loadInterstitial()
                }

                override fun onInterstitialAdShowSucceeded() {}
                override fun onInterstitialAdShowFailed(ironSourceError: IronSourceError) {}
                override fun onInterstitialAdClicked() {}
            })
        }catch (e:Exception){
            Log.d("DEEP","LoadIronsource method error")
        }
    }


    private fun showIronSourceInterstitialAd() {
        try {
            adsViewModel.onAdEvent(AdsEvent.onAdReady)
            IronSource.init(requireActivity(), ironsourceAds?.adAppKey)
            IronSource.showInterstitial()
            IronSource.setInterstitialListener(object : InterstitialListener {
                override fun onInterstitialAdReady() {
                    Log.d("DEEP","OnAdReady second isronsource")
                }
                override fun onInterstitialAdLoadFailed(ironSourceError: IronSourceError) {
                }
                override fun onInterstitialAdOpened() {
                   adsViewModel.onAdEvent(AdsEvent.onAdOpened)
                    Log.d("DEEP","IRONSOURCE ADD SHOWING")
                }
                override fun onInterstitialAdClosed() {
                    ironsource_isloaded = false
                    Log.d("DEEP","ONAdd Close")
                    adsViewModel.onAdEvent(AdsEvent.onAdClose)
                }
                override fun onInterstitialAdShowSucceeded() {
                    Log.d("DEEP","ONAdd showed")
                }
                override fun onInterstitialAdShowFailed(ironSourceError: IronSourceError) {
                    ironsource_isloaded = false
                    adsViewModel.onAdEvent(AdsEvent.onAdClose)
                    Log.d("DEEP","flage is")
                    Log.d("DEEP","IRONSURCE ADD SHOWING ERROR ${ironSourceError.errorMessage}")
                }
                override fun onInterstitialAdClicked() {}
            })
        }catch (e:Exception){
            adsViewModel.onAdEvent(AdsEvent.onAdClose)
            Log.d("DEEP","IRSONSOURCE SHOW AD ERROR")
        }
    }

    private fun loadIronSourceBannerAd() {
            adContainer?.let {adContainer ->
        if (adContainer.childCount > 0) {
            adContainer.removeAllViews()
        }
        Log.d("DEEP","load ironsource banner called")
       IronSource.destroyBanner(bannerLayout)
        IronSource.init(requireActivity(), ironsourceAds?.adAppKey)
        Log.d("DEEP","banner init")
        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE")
        bannerLayout = IronSource.createBanner(requireActivity(), ISBannerSize.BANNER)
        Log.d("DEEP","Banner created")
        adContainer.addView(bannerLayout)
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


    }
    }

    private fun loadAdMobInterstitialAd() {
        try {
            Log.d("deep","loadadmob is called")
            if(mInterstitialAd != null || admob_isloaded){
                Log.d("DEEP","Admob Intertisial load call")
                return
            }
            val adRequest = AdRequest.Builder().build()
            admobAds?.interstitialAdKey?.let  {
                InterstitialAd.load(requireContext(), it, adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            try {
                                mInterstitialAd = interstitialAd
                                admob_isloaded =true
                                Log.d("DEEP", "Admob interstitalAd add load")
                            } catch (e: Exception) {
                                Log.d("DEEP", "adMob Load Error")
                            }
                        }


                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            // Handle the error
                            try {
                                Log.d(
                                    "DEEP",
                                    "Admob interstitalAd add load Failed ${loadAdError.message}"
                                )
                                mInterstitialAd = null
                                loadFacebookInterstialAd()
                            } catch (e: Exception) {
                                Log.d("DEEP", "ADMOB Load Failed Error")
                            }
                        }
                    })
            } ?: loadFacebookInterstialAd()
        }catch (e:Exception){
            Log.d("DEEP","ADMOB SHOW FAILED")
        }
    }

    private fun showAdMobInterstialAd (){
        try {
            if (mInterstitialAd != null) {
                adsViewModel.onAdEvent(AdsEvent.onAdReady)
                Log.d("DEEP","ADMOB SHOW METHOD CALLED")
                mInterstitialAd!!.show(requireActivity())
                mInterstitialAd!!.setFullScreenContentCallback(object :
                    FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        admob_isloaded = false
                        adsViewModel.onAdEvent(AdsEvent.onAdClose)
                        Log.d("DEEP", "The ad was dismissed.")
                        val adRequest = AdRequest.Builder().build()
                        admobAds?.interstitialAdKey?.let {
                            InterstitialAd.load(requireContext(),
                                it,
                                adRequest,
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                        // The mInterstitialAd reference will be null until
                                        // an ad is loaded.
                                        Log.d("DEEP", "Admob interstitalAd add load Failed second")
                                        adsViewModel.onAdEvent(AdsEvent.onAdOpened)
                                        mInterstitialAd = interstitialAd
                                        //
                                        //                                    showad(view)

                                    }

                                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                        // Handle the error
                                        Log.i(FacebookMediationAdapter.TAG, loadAdError.message)
                                        Log.d("DEEP", "Admob interstitalAd add load Failed second")
                                        mInterstitialAd = null
                                        admob_isloaded = false
                                        adsViewModel.onAdEvent(AdsEvent.onAdClose)
                                    }

                                })
                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d("DEEP", "The ad failed to show.")
                        admob_isloaded = false
                        adsViewModel.onAdEvent(AdsEvent.onAdClose)
                    }

                    override fun onAdShowedFullScreenContent() {
                        mInterstitialAd = null
                        admob_isloaded = false
                        adsViewModel.onAdEvent(AdsEvent.onAdClose)
                        Log.d("DEEP", "Admob interstitalAd add showing")
                        Log.d("TAG", "The ad was shown.")
                    }

                })
            } else {
                admob_isloaded = false
                adsViewModel.onAdEvent(AdsEvent.onAdClose)
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }catch (e:Exception){
            adsViewModel.onAdEvent(AdsEvent.onAdClose)
            Log.d("DEEP","ADMOB SHOW ERROR")
        }

    }


    private fun loadAdMobBannerAd() {
        adContainer?.let {adContainer ->
        if (adContainer.childCount > 0) {
            adContainer.removeAllViews()
        }
        val adRequest = AdRequest.Builder().build()
        mAdView = AdView(requireContext())
        admobAds?.let { item ->
            item.bannerAdKey?.let {
                mAdView!!.adUnitId = it
                mAdView?.setAdSize(AdSize.BANNER)
                mAdView!!.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("DEEP","banner AdMob add load")
                    }
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.d("DEEP","banner Admob add load failed")
                        loadFaceBookBannerAdd()
                        //  showBannerFaceBoook();
                    }
                }
                adContainer.addView(mAdView)
                mAdView!!.loadAd(adRequest)
            } ?:loadFaceBookBannerAdd()
        } ?: loadFaceBookBannerAdd()


    }
    }

    private fun loadFacebookInterstialAd() {
        if(fb_isloaded){
            return
        }
        fbInterstitialAd = com.facebook.ads.InterstitialAd(requireContext(), fabBookAds?.interstitialAdKey)
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {

            }

            override fun onInterstitialDismissed(ad: Ad) {
            }

            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("DEEP","failed facebook  interstialAd")
                loadIronSourceInterstialAd()
                //   showAdMobInterstialAd()
            }

            override fun onAdLoaded(ad: Ad) {
                // Interstitial ad is loaded and ready to be displayed
                // Show the ad
                // fbInterstitialAd!!.show()
                fb_isloaded =true
                Log.d("DEEP","Facbook interticial add loaded")
            }

            override fun onAdClicked(ad: Ad) {}
            override fun onLoggingImpression(ad: Ad) {

            }
        }
        fbInterstitialAd?.let {
            it.loadAd(
                it.buildLoadAdConfig().withAdListener(interstitialAdListener).build()
            )
        }
    }

    private fun showFacebookinitstiaal (){
        fbInterstitialAd?.let {
            try{
                adsViewModel.onAdEvent(AdsEvent.onAdReady)
                it.show()
                Log.d("DEEP","facebook add showe")
            }catch (e:Exception){
                adsViewModel.onAdEvent(AdsEvent.onAdClose)
                Log.d("DEEP",e.message.toString())
                Log.d("DEEP","facBook add excaption")

            }
        }
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
                fb_isloaded = false
                adsViewModel.onAdEvent(AdsEvent.onAdOpened)
            }

            override fun onInterstitialDismissed(ad: Ad) {
                fb_isloaded = false
                adsViewModel.onAdEvent(AdsEvent.onAdClose)
            }

            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("DEEP","failed facebook  interstialAd")
                fb_isloaded = false
                adsViewModel.onAdEvent(AdsEvent.onAdClose)
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
        fbInterstitialAd?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)
    }

    private fun loadFaceBookBannerAdd() {
        adContainer?.let {adContainer ->
        if (adContainer.childCount > 0) {
            adContainer.removeAllViews()

        }

        val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {


            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("DEEP","Facbook banner add failed")
                loadIronSourceBannerAd()
            }

            override fun onAdLoaded(p0: Ad?) {
                Log.d("DEEP","FACBOOK BANNER ADD LOAD")
            }

            override fun onAdClicked(p0: Ad?) {
            }

            override fun onLoggingImpression(p0: Ad?) {
            }


        }
        madView = com.facebook.ads.AdView(requireContext(),
            fabBookAds?.bannerAdKey,
            com.facebook.ads.AdSize.BANNER_HEIGHT_50)

        adContainer.addView(madView)

        // madView!!.loadAd()
        madView!!.loadAd(madView!!.buildLoadAdConfig().withAdListener(adListener).build())

    }
    }


    override fun onResume() {
        super.onResume()
        Log.d("DEEP","Super class resume called")
    }

    override fun onPause() {
        super.onPause()
        bannerLayout?.let {
            IronSource.destroyBanner(it)
        }
        Log.d("DEEP","Super class paush called")
    }

    override fun onDestroy() {
        bannerLayout?.let {
            IronSource.destroyBanner(it)
        }
        Log.d("DEEP","super class destory called")
        super.onDestroy()
    }
}