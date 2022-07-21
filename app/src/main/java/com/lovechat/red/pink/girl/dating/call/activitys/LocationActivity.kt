package com.lovechat.red.pink.girl.dating.call.activitys

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ads.NewAddsActivty
import com.lovechat.red.pink.girl.dating.call.R
import com.lovechat.red.pink.girl.dating.call.adapter.FlagsAdapter
import com.lovechat.red.pink.girl.dating.call.extenstionfunctions.snackBar
import com.lovechat.red.pink.girl.dating.call.repository.Response
import com.lovechat.red.pink.girl.dating.call.videolistmodel.Falgs
import com.lovechat.red.pink.girl.dating.call.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import openOtherApp

@AndroidEntryPoint
class LocationActivity : NewAddsActivty(), FlagsAdapter.Interaction {

    val mainViewModel:MainViewModel by viewModels()
    lateinit var flagRecyclerView: RecyclerView
    lateinit var backbtn:ImageView
    lateinit var proggressBar: ProgressBar
    var videoCallType:String? = null
    val adapter = FlagsAdapter(this)

    override val adContainer: LinearLayout?
        get() = findViewById(R.id.banner_container)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val flags = listOf<Falgs>(
            Falgs(R.drawable.collage_6,"Party girl call"),
            Falgs(R.drawable.collage_7,"Naughty girl call"),
            Falgs(R.drawable.collage_8,"Sad girl call"),
            Falgs(R.drawable.collage_9,"Sweet girl call"),
           Falgs(R.drawable.collage_1,"Funny girl call"),
           Falgs(R.drawable.collage_2,"Angry girl call"),
           Falgs(R.drawable.collage_3,"Romantic girl call")
        )

        backbtn = findViewById(R.id.backbtn)
        proggressBar = findViewById(R.id.progressBar2)
        flagRecyclerView = findViewById(R.id.flagsrecycler)
        flagRecyclerView.layoutManager = LinearLayoutManager(this)
        flagRecyclerView.adapter = adapter

        backbtn.setOnClickListener {
            onBackPressed()
        }


        mainViewModel.VideoListLiveData.observe(this){
            when(it){
                is Response.Success -> {
                    proggressBar.visibility = View.GONE
                    adapter.submitList(flags)
                    Log.d("DEEP", "reposse is success ${it.videoList!!.Status}")
                    if (it.videoList!!.Data.isNotEmpty()) {
                        videoCallType = it.videoList.Settings.VideoCall

                    }else{
                        snackBar(backbtn, "Please try later")
                    }
                }
                is Response.error ->{
                    snackBar(backbtn,it.errorMassage!!)
                    proggressBar.visibility = View.GONE
                }


                is Response.Loading ->{
                    proggressBar.visibility = View.VISIBLE
                }
            }
        }


    }

    override fun onItemSelected(position: Int, item: Falgs) {
        videoCallType?.let {
            if(it == "Fail"){
                Toast.makeText(this,"Can't connect. We apologize for the inconvenience. Please try again later.",Toast.LENGTH_SHORT).show()
            }else if(it == "Prank"){
                Intent(this@LocationActivity, ChatRoomActivity::class.java).apply {
                    putExtra("flagname",item.category)
                    startActivity(this)
                }
            }else if(it == "Live"){
                Intent(this@LocationActivity, ChatRoomActivity::class.java).apply {
                    putExtra("flagname",item.category)
                    startActivity(this)
                }
            }
            else{
                permisstionDailog()
            }
        } ?: Intent(this@LocationActivity, ChatRoomActivity::class.java).apply { startActivity(this) }
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