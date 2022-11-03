package com.rugbychat.redpink.video.call.activitys

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.NativAddsActivty
import com.app.ads.AdsViewModel
import com.app.ads.utils.AdsState
import com.rugbychat.redpink.video.call.R
import com.rugbychat.redpink.video.call.databinding.ActivityStartBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import rateUs
import shareApp

@AndroidEntryPoint
class StartActivity: NativAddsActivty() {
    override val nativeAdLayout: LinearLayout?
        get() = binding.bannerContainer
    override val adContainer: LinearLayout?
        get() = null

    lateinit var binding: ActivityStartBinding
    val adsViewModel:AdsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_start)


        val backPressedCallback =object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                LeavScreenDailog.newInstance()
                    .show(this@StartActivity.supportFragmentManager, null)
            }

        }

        onBackPressedDispatcher.addCallback(backPressedCallback)

          lifecycleScope.launch {
                      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                          adsViewModel.adsState.collect{
                              when(it){
                                  AdsState.AdClosed -> {
                                      Intent(this@StartActivity, AgeActivity::class.java).apply {
                                          startActivity(this)
                                      }
                                  }
                                  AdsState.AdOpened ->{}
                                  AdsState.AdReady -> {}
                              }
                          }
                      }
                  }

        binding.apply {
            startbtn.setOnClickListener {
            showIntertisialAdd()
            }

            privecybtn.setOnClickListener {
                val policyUrl = getString(R.string.privacy_url)
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl))
                startActivity(browserIntent)
            }
            sharebtn.setOnClickListener {
                shareApp()
            }
            rateappbtn.setOnClickListener {
                rateUs()
            }
        }
    }
}