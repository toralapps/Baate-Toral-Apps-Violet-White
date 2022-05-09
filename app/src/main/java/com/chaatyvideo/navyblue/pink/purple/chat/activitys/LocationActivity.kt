package com.chaatyvideo.navyblue.pink.purple.chat.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaatyvideo.navyblue.pink.purple.chat.R
import com.chaatyvideo.navyblue.pink.purple.chat.adapter.FlagsAdapter
import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.Falgs

class LocationActivity : AppCompatActivity(), FlagsAdapter.Interaction {

    lateinit var flagRecyclerView: RecyclerView
    lateinit var backbtn:ImageView
    val adapter = FlagsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val flags = listOf<Falgs>(
            Falgs(R.drawable.india,"India"),
            Falgs(R.drawable.usa,"Usa"),
            Falgs(R.drawable.chaina,"Chaina"),
            Falgs(R.drawable.italy,"Italy"),
            Falgs(R.drawable.france,"France"),
            Falgs(R.drawable.vietnam,"Vietnam"),
            Falgs(R.drawable.united_kingdom,"United Kingdom"),
            Falgs(R.drawable.united_arab_emirates,"United Arab emirates")
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
        intent.putExtra("flagname",item.name)
        startActivity(intent)
    }
}