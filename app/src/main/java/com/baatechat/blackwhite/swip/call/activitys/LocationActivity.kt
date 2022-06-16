package com.baatechat.blackwhite.swip.call.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baatechat.blackwhite.swip.call.R
import com.baatechat.blackwhite.swip.call.adapter.FlagsAdapter
import com.baatechat.blackwhite.swip.call.videolistmodel.Falgs

class LocationActivity : AppCompatActivity(), FlagsAdapter.Interaction {

    lateinit var flagRecyclerView: RecyclerView
    lateinit var backbtn:ImageView
    val adapter = FlagsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val flags = listOf<Falgs>(
           Falgs(R.drawable.collage_1,"Model"),
           Falgs(R.drawable.collage_2,"Cute girls"),
           Falgs(R.drawable.collage_3,"love"),
           Falgs(R.drawable.collage_4,"Desi chat"),
           Falgs(R.drawable.collage_5,"Bhabhi chat/call"),
           Falgs(R.drawable.collage_6,"hot girl"),
           Falgs(R.drawable.collage_7,"Random call/chat"),
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
        val ageRange = item.category
        intent.putExtra("flagname",ageRange)
        startActivity(intent)
    }
}