package com.baatechat.blackwhite.swip.call.activitys

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
import com.baatechat.blackwhite.swip.call.MainActivity
import com.baatechat.blackwhite.swip.call.R
import com.baatechat.blackwhite.swip.call.adapter.ViewPagerAdapter
import com.baatechat.blackwhite.swip.call.extenstionfunctions.snackBar
import com.baatechat.blackwhite.swip.call.networkmanager.ConnectionLiveData
import com.baatechat.blackwhite.swip.call.repository.Response
import com.baatechat.blackwhite.swip.call.singletons.ListOfVideos
import com.baatechat.blackwhite.swip.call.videolistmodel.Data
import com.baatechat.blackwhite.swip.call.viewmodels.MainViewModel
import com.myech.video.bluepink.chat.blockuser.BlockList
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class CallNowActivity : BaseClass(), ViewPagerAdapter.Interaction {

    val mainviewmodel : MainViewModel by viewModels()
    lateinit var viewpager:ViewPager2
    lateinit var adapter: ViewPagerAdapter
    lateinit var title:TextView
    lateinit var backbtn:ImageView
    var videoList = mutableListOf<Data>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_now)




        adContainer = findViewById(R.id.banner_container)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)

        adapter = ViewPagerAdapter(this)
        viewpager = findViewById(R.id.viewpager)
        viewpager.adapter = adapter
        backbtn = findViewById(R.id.backbtn)

        backbtn.setOnClickListener {
            super.onBackPressed()
        }

        ConnectionLiveData(this).observe(this) {
            if (!it){
                snackBar(viewpager)
                return@observe
            }
            mainviewmodel.VideoListLiveData.observe(this) {
                when (it) {
                    is Response.Success -> {
                        videoList = it.videoList!!.Data as MutableList<Data>
                        adapter.submitList(it.videoList!!.Data.shuffled())
                    }
                    is Response.error -> {
                        snackBar(backbtn, it.errorMassage!!)
                    }
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        initShow()
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
                    snackBar(viewpager,mainviewmodel.report().videoList!!.Message)
                }
                is Response.error ->{
                    snackBar(viewpager,"Please try after sometimes")
                }
            }
        }
    }

    override fun call_now() {
        if(viewpager.currentItem == 0){

        }else{
            ListOfVideos.videos = videoList[viewpager.currentItem]
        }
        val intent = Intent(this@CallNowActivity, LoadingScreen::class.java)
        startActivity(intent)
    }
}