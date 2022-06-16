package com.chaatyvideo.green.black.romantic.chat.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.chip.Chip
import com.myech.video.bluepink.chat.blockuser.BlockList
import com.chaatyvideo.green.black.romantic.chat.R
import com.chaatyvideo.green.black.romantic.chat.adapter.VideoListAdapter
import com.chaatyvideo.green.black.romantic.chat.blockuser.BlockListActivity
import com.chaatyvideo.green.black.romantic.chat.extenstionfunctions.snackBar
import com.chaatyvideo.green.black.romantic.chat.networkmanager.ConnectionLiveData
import com.chaatyvideo.green.black.romantic.chat.repository.Response
import com.chaatyvideo.green.black.romantic.chat.singletons.ListOfVideos
import com.chaatyvideo.green.black.romantic.chat.videolistmodel.Data
import com.chaatyvideo.green.black.romantic.chat.viewmodels.MainViewModel
import com.videochat.letsmeetvideochat.BaseIntertisialAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatRoomActivity : BaseIntertisialAds(), VideoListAdapter.Interaction {

    val mainViewModel:MainViewModel by viewModels()
    lateinit var videolistRecyclerview:RecyclerView
    lateinit var title:TextView
    lateinit var adapter:VideoListAdapter
    lateinit var blockUser: Chip
    lateinit var backbtn:ImageView
    lateinit var flagname:TextView
    lateinit var listOfVideos:ArrayList<Data>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        getAdDataApi()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        adapter = VideoListAdapter(this)

        backbtn = findViewById(R.id.backbtn)
        blockUser = findViewById(R.id.blockusers)
        flagname = findViewById(R.id.flagname)

        val intent = intent
        flagname.text  = intent.getStringExtra("flagname")

        videolistRecyclerview = findViewById(R.id.videolistRecyclerview)
        videolistRecyclerview.setHasFixedSize(true)
        videolistRecyclerview.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        videolistRecyclerview.adapter =adapter

        backbtn.setOnClickListener {
            super.onBackPressed()
        }

        blockUser.setOnClickListener {
            initShow()
            val intent = Intent(this, BlockListActivity::class.java)
            startActivity(intent)
        }


        val connectionManager = ConnectionLiveData(applicationContext).observe(this){isConnected ->
            if(isConnected){

            }else{
                snackBar(title)
            }
        }

        mainViewModel.VideoListLiveData.observe(this){
            when(it){
                is Response.Success -> {
                    Log.d("DEEP", "reposse is success ${it.videoList!!.Status}")
                    if (it.videoList!!.Data.isNotEmpty()) {
                        val unblockUsers = getunblockedUsers(it.videoList!!.Data)
                        if (unblockUsers.isNotEmpty()) {
                            adapter.submitList(unblockUsers.shuffled())
                        } else {
                            snackBar(blockUser, "Please try later")
                        }
                    }else{
                        snackBar(blockUser, "Please try later")
                    }
                }
                is Response.error ->{
                    snackBar(blockUser,it.errorMassage!!)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        initShow()
    }

    override fun onItemSelected(position: Int, item: Data) {
        ListOfVideos.videos = item
        initShow()
        if(ListOfVideos.videos!= null) {
            val intent = Intent(this, CallNowActivity::class.java)
            startActivity(intent)
        }
    }


    private fun getunblockedUsers(videos:List<Data>?):List<Data>{
        val blockList = BlockList.getBlockVideos(this)
        val newList = mutableListOf<Data>()
        if(blockList.isNotEmpty()) {
            if (videos != null) {
                for (i in videos) {
                    Log.d("DEEP", (videos.equals(videos).toString()))
                    if (blockList.contains(i)) {
                        Log.d("DEEPcontains", i.VideoId.toString())
                    } else {
                        newList.add(i)
                    }
                }
            }
            if (newList.isEmpty()){
                return  videos!!
            }else{
                return newList
            }

        }
        return videos!!
    }
}