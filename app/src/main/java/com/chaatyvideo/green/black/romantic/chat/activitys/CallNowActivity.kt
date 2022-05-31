package com.chaatyvideo.green.black.romantic.chat.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.ncorti.slidetoact.SlideToActView
import com.chaatyvideo.green.black.romantic.chat.MainActivity
import com.chaatyvideo.green.black.romantic.chat.R
import com.chaatyvideo.green.black.romantic.chat.adapter.ViewPagerAdapter
import com.chaatyvideo.green.black.romantic.chat.extenstionfunctions.snackBar
import com.chaatyvideo.green.black.romantic.chat.repository.Response
import com.chaatyvideo.green.black.romantic.chat.singletons.ListOfVideos
import com.chaatyvideo.green.black.romantic.chat.videolistmodel.Data
import com.chaatyvideo.green.black.romantic.chat.viewmodels.MainViewModel
import com.myech.video.bluepink.chat.blockuser.BlockList
import com.videochat.letsmeetvideochat.BaseClass
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class CallNowActivity : BaseClass(), ViewPagerAdapter.Interaction {

    val mainviewmodel :MainViewModel by viewModels()
    lateinit var viewpager:ViewPager2
    lateinit var adapter:ViewPagerAdapter
    lateinit var title:TextView
    lateinit var backbtn:ImageView
    lateinit var callbtn:SlideToActView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_now)


        adContainer = findViewById(R.id.banner_container)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)

        adapter = ViewPagerAdapter(this)
        viewpager = findViewById(R.id.viewpager)
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
                    snackBar(backbtn,it.errorMassage!!)
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
        callbtn.resetSlider()
    }

    override fun onBlockUser(position: Int, item: Data) {
        blockuser(item)
    }

    fun blockuser(video: Data){
        var oldlist = ArrayList<Data>()
        if(BlockList.getBlockVideos(this).isNotEmpty()){
            oldlist = BlockList.getBlockVideos(this).also {
                it.add(video)
            }
        }else{
            oldlist.add(video)
        }
        Log.d("DEEP list size", oldlist.size.toString())
        BlockList.saveBlockVideos(this, oldlist)
        Log.d("DEEP", BlockList.getBlockVideos(this).toString())
        Toast.makeText(this,"User is blocked", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun reportUser() {
        lifecycleScope.launchWhenCreated {
            when(mainviewmodel.report()){
                is Response.Success ->{
                    snackBar(callbtn,mainviewmodel.report().videoList!!.Message)
                }
                is Response.error ->{
                    snackBar(callbtn,"Please try after sometimes")
                }
            }
        }
    }
}