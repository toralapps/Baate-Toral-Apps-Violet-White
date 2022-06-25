package com.photography.hindishayari.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photography.hindishayari.R
import com.photography.hindishayari.adapter.FlagsAdapter
import com.photography.hindishayari.videolistmodel.Falgs

class LocationActivity : AppCompatActivity(), FlagsAdapter.Interaction {

    lateinit var flagRecyclerView: RecyclerView
    lateinit var backbtn:ImageView
    val adapter = FlagsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val flags = listOf<Falgs>(
           Falgs(R.drawable.collage_1,"Romance call"),
           Falgs(R.drawable.collage_2,"Honeymoon call"),
           Falgs(R.drawable.collage_3,"Love call"),
           Falgs(R.drawable.collage_4,"Hot girl call"),
           Falgs(R.drawable.collage_5,"Live girl call")
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