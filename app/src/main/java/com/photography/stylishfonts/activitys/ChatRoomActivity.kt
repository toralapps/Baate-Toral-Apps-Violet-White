package com.photography.stylishfonts.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.chip.Chip
import com.myech.video.bluepink.chat.blockuser.BlockList
import com.photography.stylishfonts.R
import com.photography.stylishfonts.adapter.VideoListAdapter
import com.photography.stylishfonts.blockuser.BlockListActivity
import com.photography.stylishfonts.extenstionfunctions.snackBar
import com.photography.stylishfonts.networkmanager.ConnectionLiveData
import com.photography.stylishfonts.repository.Response
import com.photography.stylishfonts.singletons.ListOfVideos
import com.photography.stylishfonts.videolistmodel.Data
import com.photography.stylishfonts.viewmodels.MainViewModel
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatRoomActivity : AppCompatActivity(), VideoListAdapter.Interaction {

    val mainViewModel: MainViewModel by viewModels()
    lateinit var videolistRecyclerview:RecyclerView
    lateinit var title:TextView
    lateinit var adapter: VideoListAdapter
    lateinit var blockUser: Chip
    lateinit var backbtn:ImageView
    lateinit var flagname:TextView
    lateinit var listOfVideos:ArrayList<Data>
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        adapter = VideoListAdapter(this)
        progressBar = findViewById(R.id.progressBar)
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
            val intent = Intent(this, BlockListActivity::class.java)
            startActivity(intent)
        }


        val connectionManager = ConnectionLiveData(applicationContext).observe(this){ isConnected ->
            if(isConnected){

            }else{
                snackBar(videolistRecyclerview)
            }
        }

        mainViewModel.VideoListLiveData.observe(this){
            when(it){
                is Response.Success -> {
                    progressBar.visibility = View.GONE
                    videolistRecyclerview.visibility = View.VISIBLE
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
                    progressBar.visibility = View.GONE
                    snackBar(blockUser,it.errorMassage!!)
                }

                is Response.Loading ->{
                    videolistRecyclerview.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
            }

        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onItemSelected(position: Int, item: Data) {
        ListOfVideos.videos = item
        if(ListOfVideos.videos!= null) {
            BaseClass.showAddcounter = true
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