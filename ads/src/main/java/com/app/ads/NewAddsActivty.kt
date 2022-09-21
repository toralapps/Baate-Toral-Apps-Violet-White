package com.app.ads

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.ads.domain.models.AdsItem
import com.app.ads.domain.models.Data
import com.app.ads.utils.AdsEvent
import com.app.ads.utils.Response
import com.app.ads.utils.SavedAds
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
import com.unity3d.ads.*
import com.unity3d.mediation.*
import com.unity3d.mediation.errors.LoadError
import com.unity3d.mediation.errors.SdkInitializationError
import com.unity3d.mediation.errors.ShowError
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import kotlinx.coroutines.launch


abstract class NewAddsActivty:AppCompatActivity() {

    abstract val adContainer:LinearLayout?
    private val adsViewModel : AdsViewModel by viewModels()
    private var adsRoot: Data? = null
    var mAdView: AdView? = null
    var bannerLayout: IronSourceBannerLayout? = null
    var madView: com.facebook.ads.AdView? = null
    var fb_isloaded:Boolean=false
    var admob_isloaded:Boolean=false
    var ironsource_isloaded:Boolean=false
    var unityMediationisLoaded:Boolean = false
    var mInterstitialAd: InterstitialAd? = null
    var UnityMediationinterstitialAd:com.unity3d.mediation.InterstitialAd? = null
    var unityInterstitalAdLoaded:Boolean = false
    var fbInterstitialAd: com.facebook.ads.InterstitialAd? = null

    private var isFaceBookIntersAdsCalled:Boolean = false
    private  var isFacbookBannerAdsCalled:Boolean = false
    var fabBookAds: AdsItem? = null
    var ironsourceAds:AdsItem? = null
    var admobAds:AdsItem? = null
    var unityAds:AdsItem? = null


    private val TAG = "DEEPADSLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                adsViewModel.adsFlow.collect{ response ->
                    when (response) {
                        is Response.Success -> {
                            adsRoot = response.videoList?.data
                            SavedAds.saveApi(this@NewAddsActivty, adsRoot)
                            if(adsRoot?.showAds == true){
                                filterAdsNetworkId()
                                loadInteritialAds()
                                loadBannerAds()
                            }
                            Log.d("DEEP","response from ads")
                        }

                        is Response.error -> {
                            Log.d(TAG, response.errorMassage.toString())
                            adsRoot = SavedAds.getAds(this@NewAddsActivty)
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

        unityAds = adsRoot?.ads?.filter {
            it?.monetizationId == 5
        }?.let{
            if(it.isNotEmpty()) it.get(0) else null
        }

    }


    fun loadInteritialAds(){
        adsRoot?.let { adsRoot ->
            val firstMonitizationId = adsRoot.ads?.get(0)?.monetizationId

            if (firstMonitizationId == 1) //For Facebook Monetization
            {
                loadFacebookInterstialAd()
            }
            else if (firstMonitizationId == 2) //For AdMob
            {
                // loadFacebookInterstialAd()
                loadAdMobInterstitialAd()

            } else if (firstMonitizationId == 6) //For IronSoruce
            {
                // loadFacebookInterstialAd()
                loadIronSourceInterstialAd()

            }else if(firstMonitizationId == 5){
                //loadFacebookInterstialAd()
                initUnityintestitalAds()

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
                    //loadFaceBookBannerAdd()
                    loadAdMobBannerAd()

                } else if (firstMonitizationId == 6) //For IronSoruce
                {
                    //loadFaceBookBannerAdd()
                    loadIronSourceBannerAd()
                }else if(firstMonitizationId == 5){
                    //loadFaceBookBannerAdd()
                    loadUnityBannerAd()
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
            }else if(unityInterstitalAdLoaded){
                Log.d("DEEP","UntyAd SHOW CALL")
                showUnityinterstitalAds()
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
            IronSource.init(this, ironsourceAds?.adAppKey)
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

    fun loadUnityBannerAd(){
        adContainer?.let {
            if (it.childCount > 0) {
                it.removeAllViews()
            }
            Log.d("DEEP","unity called")
            UnityAds.initialize(getApplicationContext(), unityAds?.adAppKey, false)
            val unityBannerListener = object : BannerView.Listener(){
                override fun onBannerLoaded(bannerAdView: BannerView?) {
                    super.onBannerLoaded(bannerAdView)
                    Log.d("DEEP","UnityBAner Add loaded")
                }

                override fun onBannerFailedToLoad(
                    bannerAdView: BannerView?,
                    errorInfo: BannerErrorInfo?,
                ) {
                    Log.d("DEEP","Unity banner Add load failed")
                }
            }

            val unityAdview = BannerView(this,unityAds?.bannerAdKey, UnityBannerSize(320,50))
            unityAdview.load()
            it.addView(unityAdview)
        }


    }

    private fun showIronSourceInterstitialAd() {
        try {
            adsViewModel.onAdEvent(AdsEvent.onAdReady)
            IronSource.init(this, ironsourceAds?.adAppKey)
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
                    adsViewModel.onAdEvent(AdsEvent.onAdClose)
                }
                override fun onInterstitialAdShowSucceeded() {
                    ironsource_isloaded = false
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
            IronSource.init(this, ironsourceAds?.adAppKey)
            Log.d("DEEP","banner init")
            IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE")
            IronSource.setMetaData("UnityAds_coppa","true")
            bannerLayout = IronSource.createBanner(this, ISBannerSize.BANNER)
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
            if(mInterstitialAd != null || admob_isloaded){
                Log.d("DEEP","Admob Intertisial load call")
                return
            }
            val adRequest = AdRequest.Builder().build()
            admobAds?.interstitialAdKey?.let  {
                InterstitialAd.load(this@NewAddsActivty, it, adRequest,
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
                mInterstitialAd!!.show(this@NewAddsActivty)
                mInterstitialAd!!.setFullScreenContentCallback(object :
                    FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        admob_isloaded = false
                        adsViewModel.onAdEvent(AdsEvent.onAdClose)
                        Log.d("DEEP", "The ad was dismissed.")
                        val adRequest = AdRequest.Builder().build()
                        admobAds?.interstitialAdKey?.let {
                            InterstitialAd.load(this@NewAddsActivty,
                                it,
                                adRequest,
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                        // The mInterstitialAd reference will be null until
                                        // an ad is loaded.
                                        Log.d("DEEP", "Admob interstitalAd add load Failed second")
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
                        } ?: adsViewModel.onAdEvent(AdsEvent.onAdClose)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d("DEEP", "The ad failed to show.")
                        admob_isloaded = false
                        adsViewModel.onAdEvent(AdsEvent.onAdClose)
                    }

                    override fun onAdShowedFullScreenContent() {
                        mInterstitialAd = null
                        admob_isloaded = false
                        adsViewModel.onAdEvent(AdsEvent.onAdOpened)
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


    @SuppressLint("MissingPermission")
    private fun loadAdMobBannerAd() {

        adContainer?.let{adContainer ->
            if (adContainer.childCount > 0) {
                adContainer.removeAllViews()
            }
            val adRequest = AdRequest.Builder().build()
            mAdView = AdView(applicationContext)
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
            Log.d("DEEP","fb_isloded true return calledd")
            return
        }else if(isFaceBookIntersAdsCalled){
            Log.d("DEEP","isFaceBookAddCalled true return calledd")
            initUnityintestitalAds()
            return
        }
        fbInterstitialAd = com.facebook.ads.InterstitialAd(this, fabBookAds?.interstitialAdKey)
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {

            }

            override fun onInterstitialDismissed(ad: Ad) {
            }

            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("DEEP","failed facebook  interstialAd")
                isFaceBookIntersAdsCalled = true
                initUnityintestitalAds()
                //   showAdMobInterstialAd()
            }

            override fun onAdLoaded(ad: Ad) {
                // Interstitial ad is loaded and ready to be displayed
                // Show the ad
                // fbInterstitialAd!!.show()
                fb_isloaded =true
                isFaceBookIntersAdsCalled = true
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
        } ?: adsViewModel.onAdEvent(AdsEvent.onAdClose)
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
                fb_isloaded = false
                adsViewModel.onAdEvent(AdsEvent.onAdOpened)
            }

            override fun onInterstitialDismissed(ad: Ad) {
                isFaceBookIntersAdsCalled = false
                fb_isloaded = false
                adsViewModel.onAdEvent(AdsEvent.onAdClose)
            }

            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                Log.d("DEEP","failed facebook  interstialAd")
                isFaceBookIntersAdsCalled = false
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

        if(isFacbookBannerAdsCalled){
            Log.d("DEEP","isFacbookbaneerAds true return calledd")
            return
        }

        adContainer?.let {adContainer ->
            if (adContainer.childCount > 0) {
                adContainer.removeAllViews()

            }

            val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {


                override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                    Log.d("DEEP","Facbook banner add failed")
                    isFacbookBannerAdsCalled = true
                    loadIronSourceBannerAd()
                }

                override fun onAdLoaded(p0: Ad?) {
                    isFacbookBannerAdsCalled = true
                    Log.d("DEEP","FACBOOK BANNER ADD LOAD")
                }

                override fun onAdClicked(p0: Ad?) {
                }

                override fun onLoggingImpression(p0: Ad?) {

                }

            }
            madView = com.facebook.ads.AdView(this,
                fabBookAds?.bannerAdKey,
                com.facebook.ads.AdSize.BANNER_HEIGHT_50)

            adContainer.addView(madView)

            // madView!!.loadAd()
            madView!!.loadAd(madView!!.buildLoadAdConfig().withAdListener(adListener).build())

        }
    }


    fun loadMediationUnityInterstitialAd(){
        unityAds?.let {unityads ->

            if(unityMediationisLoaded){
                Log.d("DEEP","Unity Ad is loaded return called")
                return
            }
            UnityMediationinterstitialAd =
                unityads.interstitialAdKey?.let { com.unity3d.mediation.InterstitialAd(this, "interstitialAdUnitId") }

            val loadListener: IInterstitialAdLoadListener = object : IInterstitialAdLoadListener {
                override fun onInterstitialLoaded(p0: com.unity3d.mediation.InterstitialAd?) {
                    unityMediationisLoaded = true
                    Log.d("DEEP","unity interstitialAds loaded")
                }

                override fun onInterstitialFailedLoad(
                    p0: com.unity3d.mediation.InterstitialAd?,
                    p1: LoadError?,
                    p2: String?,
                ) {
                    UnityMediationinterstitialAd = null
                    loadIronSourceInterstialAd()
                    Log.d("DEEP","unity interstitialAds load failed ${p2}")
                }
            }
            UnityMediationinterstitialAd?.load(loadListener)
        }

    }

    private fun initUnityintestitalAds(){
        unityAds?.let {unityads ->
            UnityAds.initialize(this,unityads.adAppKey,false,object :IUnityAdsInitializationListener{
                override fun onInitializationComplete() {
                    loadUnityinterstialAds()
                }

                override fun onInitializationFailed(
                    error: UnityAds.UnityAdsInitializationError?,
                    message: String?,
                ) {
                    loadIronSourceInterstialAd()
                    Log.d("DEEP","Unity initialization failed ${message.toString()}")
                }

            })

        }
    }

    private fun loadUnityinterstialAds() {
        if (unityInterstitalAdLoaded) {
            Log.d("DEEP","return to call from unityinterstial")
            return
        }
        val loadListener = object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String?) {
                unityInterstitalAdLoaded = true
            }

            override fun onUnityAdsFailedToLoad(
                placementId: String?,
                error: UnityAds.UnityAdsLoadError?,
                message: String?,
            ) {
                Log.e("UnityAdsExample", "Unity Ads failed to load ad for $placementId with error: [$error] $message");
                loadIronSourceInterstialAd()
            }
        }
        unityAds?.let {
            UnityAds.load(it.interstitialAdKey,loadListener)
        }
    }

    private fun showUnityinterstitalAds(){
        adsViewModel.onAdEvent(AdsEvent.onAdReady)
        val showListener = object :IUnityAdsShowListener{
            override fun onUnityAdsShowFailure(
                placementId: String?,
                error: UnityAds.UnityAdsShowError?,
                message: String?,
            ) {
                unityInterstitalAdLoaded = false
                Log.d("UnityAdsExample", "Unity Ads failed to show ad for $placementId with error: [$error] $message")
                adsViewModel.onAdEvent(AdsEvent.onAdClose)
            }

            override fun onUnityAdsShowStart(placementId: String?) {
                Log.d("UnityAdsExample", "onUnityAdsShowStart: $placementId");
                adsViewModel.onAdEvent(AdsEvent.onAdOpened)

            }

            override fun onUnityAdsShowClick(placementId: String?) {
                unityInterstitalAdLoaded = false

                Log.d("UnityAdsExample", "onUnityAdsShowClick: $placementId");
            }

            override fun onUnityAdsShowComplete(
                placementId: String?,
                state: UnityAds.UnityAdsShowCompletionState?,
            ) {
                unityInterstitalAdLoaded = false
                Log.d("UnityAdsExample", "onUnityAdsShowComplete: " + placementId);
                adsViewModel.onAdEvent(AdsEvent.onAdClose)
            }

        }
        unityAds?.let {
            UnityAds.show(this, it.interstitialAdKey, UnityAdsShowOptions(), showListener)
        } ?: adsViewModel.onAdEvent(AdsEvent.onAdClose)

    }

    fun showMediationUnityinterstitalAds(){
        UnityMediationinterstitialAd?.let {
            adsViewModel.onAdEvent(AdsEvent.onAdReady)
            val showListner = object : IInterstitialAdShowListener{
                override fun onInterstitialShowed(p0: com.unity3d.mediation.InterstitialAd?) {
                    adsViewModel.onAdEvent(AdsEvent.onAdOpened)
                    Log.d("DEEP","The unityad has started to show.")
                }

                override fun onInterstitialClicked(p0: com.unity3d.mediation.InterstitialAd?) {
                    Log.d("DEEP"," The user has selected the unityad.")
                }

                override fun onInterstitialClosed(p0: com.unity3d.mediation.InterstitialAd?) {
                    Log.d("DEEP","The unityAd has finished showing")
                    adsViewModel.onAdEvent(AdsEvent.onAdClose)
                }

                override fun onInterstitialFailedShow(
                    p0: com.unity3d.mediation.InterstitialAd?,
                    p1: ShowError?,
                    p2: String?,
                ) {
                    Log.d("DEEP","An error occurred during the unityAd showtime.")
                    adsViewModel.onAdEvent(AdsEvent.onAdClose)
                }

            }
        }?: adsViewModel.onAdEvent(AdsEvent.onAdClose)

    }

    private fun initUnityMediationSdk(){
        unityAds?.let {unityads ->
            val configuration = InitializationConfiguration.builder()
                .setGameId(unityads.adAppKey)
                .setInitializationListener(object : IInitializationListener {
                    override fun onInitializationComplete() {
                        // Unity Mediation is initialized. Try loading an ad.
                        Log.d("DEEP","Unity Mediation is successfully initialized.")
                    }

                    override fun onInitializationFailed(errorCode: SdkInitializationError?, msg: String?) {
                        // Unity Mediation failed to initialize. Printing failure reason...
                        Log.d("DEEP","Unity Mediation Failed to Initialize : $msg")
                    }
                }).build()

            UnityMediation.initialize(configuration)

        }

    }

    override fun onPause() {
        super.onPause()
        bannerLayout?.let {
            IronSource.destroyBanner(it)
        }
        isFacbookBannerAdsCalled = false
        isFaceBookIntersAdsCalled = false
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