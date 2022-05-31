package com.chaatyvideo.green.black.romantic.chat.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.material.card.MaterialCardView
import com.chaatyvideo.green.black.romantic.chat.R
import com.videochat.letsmeetvideochat.BaseClass

class GenderActivity : BaseClass() {
    lateinit var maleradiobtncard:ImageView
    lateinit var femaleradiobtncard:ImageView
    lateinit var nextbtn:Button
    lateinit var backbtn:ImageView
    var perviousIteam:ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)

        adContainer = findViewById(R.id.banner_container)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)

        maleradiobtncard = findViewById(R.id.malecard)
        femaleradiobtncard = findViewById(R.id.femalecard)
        backbtn = findViewById(R.id.backbtn)
        nextbtn = findViewById(R.id.elevatedButton)


        backbtn.setOnClickListener {
            super.onBackPressed()
        }

        nextbtn.setOnClickListener {
            val intent = Intent(this,LocationActivity::class.java)
            startActivity(intent)
        }

        femaleradiobtncard.setOnClickListener {
            femaleradiobtncard.setImageResource(R.drawable.female_selected)
            maleradiobtncard.setImageResource(R.drawable.malebg_green)
        }

        maleradiobtncard.setOnClickListener {
            maleradiobtncard.setImageResource(R.drawable.male_selected)
            femaleradiobtncard.setImageResource(R.drawable.femalebg_green)
        }

    }

    fun deselectPerivousItem(img: ImageView){
        perviousIteam?.isSelected = false
        img.isSelected = true
        perviousIteam = img
//        selected = false
    }
}