package com.chaatyvideo.green.black.romantic.chat.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaatyvideo.green.black.romantic.chat.R
import com.chaatyvideo.green.black.romantic.chat.adapter.FlagsAdapter
import com.chaatyvideo.green.black.romantic.chat.videolistmodel.Falgs

class LocationActivity : AppCompatActivity(), FlagsAdapter.Interaction {

    lateinit var flagRecyclerView: RecyclerView
    lateinit var backbtn:ImageView
    val adapter = FlagsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val flags = listOf<Falgs>(
           Falgs(R.drawable.imge1,"18","25"),
           Falgs(R.drawable.img2,"25","32"),
           Falgs(R.drawable.img3,"32","45"),
           Falgs(R.drawable.img4,"45","65"),
        )

        backbtn = findViewById(R.id.backbtn)
        flagRecyclerView = findViewById(R.id.flagsrecycler)
        flagRecyclerView.layoutManager = LinearLayoutManager(this)
        flagRecyclerView.adapter = adapter
        adapter.submitList(flags)

        backbtn.setOnClickListener {
            super.onBackPressed()
        }

    }

    override fun onItemSelected(position: Int, item: Falgs) {
        val intent = Intent(this@LocationActivity, ChatRoomActivity::class.java)
        val ageRange = "${item.startAge} to ${item.endAge} age"
        intent.putExtra("flagname",ageRange)
        startActivity(intent)
    }
}