package com.toralapps.viowh.livecall.activitys

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.app.ads.NewAddsActivty
import com.toralapps.viowh.livecall.R
import com.toralapps.viowh.livecall.adapter.FlagsAdapter
import com.toralapps.viowh.livecall.extenstionfunctions.snackBar
import com.toralapps.viowh.livecall.repository.Response
import com.toralapps.viowh.livecall.singletons.ListOfVideos
import com.toralapps.viowh.livecall.videolistmodel.Falgs
import com.toralapps.viowh.livecall.videolistmodel.SavedVideoList
import com.toralapps.viowh.livecall.videolistmodel.VideoList
import com.toralapps.viowh.livecall.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import openOtherApp

@AndroidEntryPoint
class LocationActivity : NewAddsActivty(), FlagsAdapter.Interaction {

    val mainViewModel: MainViewModel by viewModels()
    lateinit var flagRecyclerView: ViewPager2
    lateinit var backbtn:ImageView
    lateinit var proggressBar: ProgressBar
    var videoCallType:String? = null
    val adapter = FlagsAdapter(this)

    override val adContainer: LinearLayout?
        get() = findViewById(R.id.banner_container)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        val flags = listOf<Falgs>(
            Falgs(R.drawable.collage_4,"Musician girl call"),
            Falgs(R.drawable.collage_6,"Lawyer girl call"),
            Falgs(R.drawable.collage_8,"Doctor girl call"),
           Falgs(R.drawable.collage_1,"Mechanic girl call")
        )

        backbtn = findViewById(R.id.backbtn)
        proggressBar = findViewById(R.id.progressBar2)
        flagRecyclerView = findViewById(R.id.flagsViewPager)
        flagRecyclerView.adapter = adapter

        backbtn.setOnClickListener {
            onBackPressed()
        }


        flagRecyclerView.apply {
            this.clipToPadding = false
            this.clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer{ page, position ->
                val r = 1 - Math.abs(position)
                page.scaleY=0.85f + r * 0.15f
            }
        }


        flagRecyclerView.setPageTransformer(compositePageTransformer)


        mainViewModel.VideoListLiveData.observe(this){
            when(it){
                is Response.Success -> {
                    proggressBar.visibility = View.GONE
                    adapter.submitList(flags)
                    Log.d("DEEP", "reposse is success ${it.videoList!!.Status}")
                    if (it.videoList!!.Data.isNotEmpty()) {
                       setUpVideoCallType(it.videoList.Settings.VideoCall,it.videoList)
                    }else{
                        snackBar(backbtn, "Please try later")
                    }
                }
                is Response.error ->{
                    if (ListOfVideos.selectedIndex != ListOfVideos.ListOfApis.size){
                        Log.d("RECALLING", "onCreate: reCallingApi ${ListOfVideos.selectedIndex}")
                        ListOfVideos.setNextApi()
                        mainViewModel.reCallingApi()
                    }else{
                        snackBar(backbtn,it.errorMassage!!)
                        proggressBar.visibility = View.GONE
                    }
                }

                is Response.Loading ->{
                    proggressBar.visibility = View.VISIBLE
                }
            }
        }


    }


    private fun setUpVideoCallType(videoCall: String, videoList: VideoList) {
        videoCall.let {
            if(it == "Fail"){
                videoCallType = videoCall
            }else if(it == "Prank"){
                SavedVideoList.saveVideos(this,videoList)
                videoCallType = videoCall
            }else if(it == "Live"){
                SavedVideoList.saveVideos(this,videoList)
                videoCallType = videoCall
            }else if (it == "Cache"){
                val video = SavedVideoList.getVideos(this)
                videoCallType = video?.let {
                    Log.d("DEEP","shreddata is not null")
                     it.Settings.VideoCall
                } ?: "Prank"
            }else{
                SavedVideoList.saveVideos(this,videoList)
                videoCallType = videoCall
            }
        }
    }

    override fun onItemSelected(position: Int, item: Falgs) {
        if(SavedVideoList.getVideos(this) != null) {
            videoCallType?.let {
                if (it == "Fail") {
                    Toast.makeText(this,
                        "Can't connect. We apologize for the inconvenience. Please try again later.",
                        Toast.LENGTH_SHORT).show()
                } else if (it == "Prank") {
                    Intent(this@LocationActivity, ChatRoomActivity::class.java).apply {
                        putExtra("flagname", item.category)
                        startActivity(this)
                    }
                } else if (it == "Live") {
////                    Intent(this@LocationActivity, ConnectLiveActivity::class.java).apply {
////                        startActivity(this)
//                    }
                    Intent(this@LocationActivity, ChatRoomActivity::class.java).apply {
                        putExtra("flagname", item.category)
                        startActivity(this)
                    }
                }  else {
                    permisstionDailog()
                }
            } ?:   Intent(this@LocationActivity, ChatRoomActivity::class.java).apply {
                        putExtra("flagname", item.category)
                        startActivity(this)
            }
        } else {
            Toast.makeText(this,
                "Can't connect. We apologize for the inconvenience. Please try again later.",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun closeBtnClick(position: Int, item: Falgs) {
        flagRecyclerView.setCurrentItem(flagRecyclerView.currentItem +1)
    }

    override fun onNextClick(position: Int, item: Falgs) {
        flagRecyclerView.currentItem = flagRecyclerView.currentItem +1
    }

    override fun reportBtnClick() {
        Toast.makeText(this,"We Will check this content within 24 hours",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this,"You need to download this app to continue",Toast.LENGTH_SHORT).show()
            })

        val dialog: AlertDialog = alertDialogBuilder.create()
        dialog.show()
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}