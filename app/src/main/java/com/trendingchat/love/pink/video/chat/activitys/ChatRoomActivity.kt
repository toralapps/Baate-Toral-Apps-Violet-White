package com.trendingchat.love.pink.video.chat.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.myech.video.bluepink.chat.blockuser.BlockList
import com.trendingchat.love.pink.video.chat.R
import com.trendingchat.love.pink.video.chat.adapter.VideoListAdapter
import com.trendingchat.love.pink.video.chat.blockuser.BlockListActivity
import com.trendingchat.love.pink.video.chat.extenstionfunctions.snackBar
import com.trendingchat.love.pink.video.chat.networkmanager.ConnectionLiveData
import com.trendingchat.love.pink.video.chat.repository.Response
import com.trendingchat.love.pink.video.chat.singletons.ListOfVideos
import com.trendingchat.love.pink.video.chat.videolistmodel.Data
import com.trendingchat.love.pink.video.chat.viewmodels.MainViewModel
import com.videochat.letsmeetvideochat.BaseIntertisialAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatRoomActivity : BaseIntertisialAds(), VideoListAdapter.Interaction {

    val mainViewModel:MainViewModel by viewModels()
    lateinit var videolistRecyclerview:RecyclerView
    lateinit var title:TextView
    lateinit var adapter:VideoListAdapter
    lateinit var blockUser: Chip
    lateinit var listOfVideos:ArrayList<Data>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        adapter = VideoListAdapter(this)

        title = findViewById(R.id.title)
        blockUser = findViewById(R.id.blockusers)
        videolistRecyclerview = findViewById(R.id.videolistRecyclerview)
        videolistRecyclerview.layoutManager = LinearLayoutManager(this)
        videolistRecyclerview.adapter =adapter

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
                    val unblockUsers = getunblockedUsers(it.videoList!!.Data)
                    if (unblockUsers.isNotEmpty()) {
                        adapter.submitList(unblockUsers.shuffled())
                    }else{
                        snackBar(title,"Please try later")
                    }
                }
                is Response.error ->{
                    snackBar(title,it.errorMassage!!)
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