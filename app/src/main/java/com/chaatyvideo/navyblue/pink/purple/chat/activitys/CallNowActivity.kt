package com.chaatyvideo.navyblue.pink.purple.chat.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.ncorti.slidetoact.SlideToActView
import com.chaatyvideo.navyblue.pink.purple.chat.MainActivity
import com.chaatyvideo.navyblue.pink.purple.chat.R
import com.chaatyvideo.navyblue.pink.purple.chat.adapter.ViewPagerAdapter
import com.chaatyvideo.navyblue.pink.purple.chat.extenstionfunctions.snackBar
import com.chaatyvideo.navyblue.pink.purple.chat.repository.Response
import com.chaatyvideo.navyblue.pink.purple.chat.singletons.ListOfVideos
import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.Data
import com.chaatyvideo.navyblue.pink.purple.chat.viewmodels.MainViewModel
import com.videochat.letsmeetvideochat.BaseClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallNowActivity : BaseClass() {

    val mainviewmodel :MainViewModel by viewModels()
    lateinit var viewpager:ViewPager2
    lateinit var adapter:ViewPagerAdapter
    lateinit var title:TextView
    lateinit var backbtn:ImageView
    lateinit var callbtn:SlideToActView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_now)

        adapter = ViewPagerAdapter()
        viewpager = findViewById(R.id.viewpager)
        title = findViewById(R.id.title)
        var videoList = mutableListOf<Data>()
        viewpager.adapter = adapter
        callbtn = findViewById(R.id.callbtn)
        backbtn = findViewById(R.id.backbtn)

        backbtn.setOnClickListener {
            super.onBackPressed()
        }

        mainviewmodel.VideoListLiveData.observe(this){
            when(it){
                is Response.Success ->{
                    videoList = it.videoList!!.Data as MutableList<Data>
                    adapter.submitList(it.videoList!!.Data.shuffled())
                }
                is Response.error ->{
                    snackBar(title,it.errorMassage!!)
                }
            }
        }



        callbtn.onSlideCompleteListener =(object :SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                if(viewpager.currentItem == 0){

                }else{
                    ListOfVideos.videos = videoList[viewpager.currentItem]
                }
                val intent = Intent(this@CallNowActivity,MainActivity::class.java)
                startActivity(intent)
            }

        })



    }

    override fun onResume() {
        super.onResume()
        initShow()
        adContainer = findViewById(R.id.banner_container)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)
        initLoad()
        getAdDataApi()
        callbtn.resetSlider()
    }
}