package com.trendingchat.love.pink.video.chat.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.ncorti.slidetoact.SlideToActView
import com.trendingchat.love.pink.video.chat.MainActivity
import com.trendingchat.love.pink.video.chat.R
import com.trendingchat.love.pink.video.chat.adapter.ViewPagerAdapter
import com.trendingchat.love.pink.video.chat.extenstionfunctions.snackBar
import com.trendingchat.love.pink.video.chat.repository.Response
import com.trendingchat.love.pink.video.chat.singletons.ListOfVideos
import com.trendingchat.love.pink.video.chat.videolistmodel.Data
import com.trendingchat.love.pink.video.chat.videolistmodel.VideoList
import com.trendingchat.love.pink.video.chat.viewmodels.MainViewModel
import com.videochat.letsmeetvideochat.BaseClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallNowActivity : BaseClass() {

    val mainviewmodel :MainViewModel by viewModels()
    lateinit var viewpager:ViewPager2
    lateinit var adapter:ViewPagerAdapter
    lateinit var title:TextView
    lateinit var leftArrowBtn:ImageView
    lateinit var rightArrowBtn:ImageView
    lateinit var callbtn:SlideToActView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_now)

        leftArrowBtn = findViewById(R.id.leftArrow)
        rightArrowBtn = findViewById(R.id.rightArrow)
        adapter = ViewPagerAdapter()
        viewpager = findViewById(R.id.viewpager)
        title = findViewById(R.id.title)
        var videoList = mutableListOf<Data>()
        viewpager.adapter = adapter
        callbtn = findViewById(R.id.callbtn)

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



        leftArrowBtn.setOnClickListener {
            viewpager.setCurrentItem(viewpager.currentItem -1)

        }

        rightArrowBtn.setOnClickListener {
            viewpager.setCurrentItem(viewpager.currentItem +1)
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