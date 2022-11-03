package com.rugbychat.redpink.video.call.activitys

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.app.ads.NewAddsActivty
import com.rugbychat.redpink.video.call.R
import com.rugbychat.redpink.video.call.adapter.ViewPagerAdapter
import com.rugbychat.redpink.video.call.extenstionfunctions.snackBar
import com.rugbychat.redpink.video.call.livevideocall.activity.ConnectLiveActivity
import com.rugbychat.redpink.video.call.networkmanager.ConnectionLiveData
import com.rugbychat.redpink.video.call.repository.Response
import com.rugbychat.redpink.video.call.singletons.ListOfVideos
import com.rugbychat.redpink.video.call.videolistmodel.Data
import com.rugbychat.redpink.video.call.viewmodels.MainViewModel
import com.myech.video.bluepink.chat.blockuser.BlockList
import dagger.hilt.android.AndroidEntryPoint
import openOtherApp
import java.util.ArrayList

@AndroidEntryPoint
class CallNowActivity : NewAddsActivty(), ViewPagerAdapter.Interaction {

    val mainviewmodel : MainViewModel by viewModels()
    lateinit var viewpager:ViewPager2
    lateinit var adapter: ViewPagerAdapter
    lateinit var title:TextView
    lateinit var backbtn:ImageView
    var videoList = mutableListOf<Data>()
    var videoCallType:String? = null
    override val adContainer: LinearLayout?
        get() = findViewById(R.id.banner_container)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_now)




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
                    else -> {}
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
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
                else -> {}
            }
        }
    }

    override fun call_now() {
        if(viewpager.currentItem == 0){

        }else{
            ListOfVideos.videos = videoList[viewpager.currentItem]
        }
        when(intent.getStringExtra("videocalltype")){

            "Live" ->{
                val intent = Intent(this, ConnectLiveActivity::class.java)
                startActivity(intent)
            }
            "Prank" ->{
                val intent = Intent(this, LoadingScreen::class.java)
                startActivity(intent)
            }
            "Fail" ->{
                Toast.makeText(this,"Can't connect. We apologize for the inconvenience. Please try again later.",Toast.LENGTH_SHORT).show()
            }
            "Cache" ->{
                val intent = Intent(this, LoadingScreen::class.java)
                startActivity(intent)
            }
            else ->{
                intent.getStringExtra("videocalltype")?.let { it1 -> videoCallType = it1 }
                permisstionDailog()
            }


        }

    }

    fun permisstionDailog(){
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Download new version")
        alertDialogBuilder.setMessage("Please download the new version of this app to continue.")
        alertDialogBuilder.setPositiveButton("DownloadApp",
            DialogInterface.OnClickListener { dialogInterface, i ->
                videoCallType?.let { openOtherApp(it) }
            })
        alertDialogBuilder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialogInterface, i ->
                Log.d(
                    "NagavtiveButton",
                    "onClick: Cancelling"
                )
                snackBar(backbtn,"You need to download this app to continue")
            })

        val dialog: AlertDialog = alertDialogBuilder.create()
        dialog.show()
    }
}