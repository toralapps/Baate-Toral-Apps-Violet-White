package com.trendingchat.love.pink.video.chat.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trendingchat.love.pink.video.chat.R
import com.trendingchat.love.pink.video.chat.adapter.VideoListAdapter
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
    lateinit var listOfVideos:ArrayList<Data>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        adapter = VideoListAdapter(this)

        title = findViewById(R.id.title)
        videolistRecyclerview = findViewById(R.id.videolistRecyclerview)
        videolistRecyclerview.layoutManager = LinearLayoutManager(this)
        videolistRecyclerview.adapter =adapter






        val connectionManager = ConnectionLiveData(applicationContext).observe(this){isConnected ->
            if(isConnected){

            }else{
                snackBar(title)
            }
        }

        mainViewModel.VideoListLiveData.observe(this){
            when(it){
                is Response.Success ->{
                    Log.d("DEEP","reposse is success ${it.videoList!!.Status}")
                    listOfVideos = it.videoList!!.Data as ArrayList<Data>
                    adapter.submitList(it.videoList!!.Data.shuffled())
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
}