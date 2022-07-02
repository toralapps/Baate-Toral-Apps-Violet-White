package com.baatechat.bluewhite.girlvideo.call.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baatechat.bluewhite.girlvideo.call.R
import com.baatechat.bluewhite.girlvideo.call.adapter.FlagsAdapter
import com.baatechat.bluewhite.girlvideo.call.videolistmodel.Falgs

class LocationActivity : AppCompatActivity(), FlagsAdapter.Interaction {

    lateinit var flagRecyclerView: RecyclerView
    lateinit var backbtn:ImageView
    val adapter = FlagsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val flags = listOf<Falgs>(
            Falgs(R.drawable.collage_6,"Asian girls"),
            Falgs(R.drawable.collage_7,"Nepali girls"),
            Falgs(R.drawable.collage_8,"Indian girls"),
            Falgs(R.drawable.collage_9,"Pakistani girls"),
           Falgs(R.drawable.collage_1,"Canadian girls"),
           Falgs(R.drawable.collage_2,"Chinese girls"),
           Falgs(R.drawable.collage_3,"Chinese girls"),
           Falgs(R.drawable.collage_4,"Japanese girls"),
           Falgs(R.drawable.collage_5,"Russian girls")
        )

        backbtn = findViewById(R.id.backbtn)
        flagRecyclerView = findViewById(R.id.flagsrecycler)
        flagRecyclerView.layoutManager = LinearLayoutManager(this)
        flagRecyclerView.adapter = adapter
        adapter.submitList(flags)

        backbtn.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onItemSelected(position: Int, item: Falgs) {
        val intent = Intent(this@LocationActivity, ChatRoomActivity::class.java)
        val ageRange = item.category
        intent.putExtra("flagname",ageRange)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        BaseClass.showAddcounter = false
    }
}