package com.toralapps.viowh.livecall.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ads.AdsViewModel
import com.app.ads.NewAddsActivty
import com.app.ads.utils.AdsState
import com.google.android.material.chip.Chip
import com.toralapps.viowh.livecall.R
import com.myech.video.bluepink.chat.blockuser.BlockList
import com.toralapps.viowh.livecall.adapter.VideoListAdapter
import com.toralapps.viowh.livecall.blockuser.BlockListActivity
import com.toralapps.viowh.livecall.extenstionfunctions.snackBar
import com.toralapps.viowh.livecall.networkmanager.ConnectionLiveData
import com.toralapps.viowh.livecall.repository.Response
import com.toralapps.viowh.livecall.singletons.ListOfVideos
import com.toralapps.viowh.livecall.videolistmodel.Data
import com.toralapps.viowh.livecall.videolistmodel.SavedVideoList
import com.toralapps.viowh.livecall.videolistmodel.VideoList
import com.toralapps.viowh.livecall.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatRoomActivity : NewAddsActivty(), VideoListAdapter.Interaction {
    val adsViewModel : AdsViewModel by viewModels()
    val mainViewModel: MainViewModel by viewModels()
    lateinit var videolistRecyclerview:RecyclerView
    lateinit var title:TextView
    lateinit var adapter: VideoListAdapter
    lateinit var blockUser: Chip
    lateinit var backbtn:ImageView
    lateinit var flagname:TextView
    lateinit var listOfVideos:ArrayList<Data>
    lateinit var progressBar: ProgressBar
    var videoCallType:String? = null

    override val adContainer: LinearLayout?
        get() = findViewById(R.id.banner_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)



        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                adsViewModel.adsState.collect{adsState ->
                    when(adsState){
                        is AdsState.AdOpened ->{

                        }

                        is AdsState.AdClosed ->{
                            val intent = Intent(this@ChatRoomActivity, CallNowActivity::class.java)
                            videoCallType?.let {
                                intent.putExtra("videocalltype", videoCallType)
                            }
                            startActivity(intent)
                        }

                        is AdsState.AdReady ->{

                        }
                    }
                }
            }
        }


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
        videolistRecyclerview.layoutManager = GridLayoutManager(this,2)
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
                  setUpRecyclerView(it.videoList)
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

    fun setUpRecyclerView(video: VideoList?) {
        progressBar.visibility = View.GONE
        videolistRecyclerview.visibility = View.VISIBLE
        video?.let { videoList ->
            Log.d("DEEP", "reposse is success ${videoList.Status}")
            if (videoList.Data.isNotEmpty()) {
                videoCallType = videoList.Settings.VideoCall
                if (videoCallType == "Cache"){
                    videoCallType =  SavedVideoList.getVideos(this)?.let {
                         it.Settings.VideoCall
                    } ?: "Prank"
                    }
                val unblockUsers = getunblockedUsers(videoList.Data)
                 if (unblockUsers.isNotEmpty()) {
                    adapter.submitList(unblockUsers.shuffled())
                } else {
                    snackBar(blockUser, "Please try later")
                }
            }else{
                snackBar(blockUser, "Please try later")
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onItemSelected(position: Int, item: Data) {
        ListOfVideos.videos = item
        if(ListOfVideos.videos!= null) {
            showIntertisialAdd()
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