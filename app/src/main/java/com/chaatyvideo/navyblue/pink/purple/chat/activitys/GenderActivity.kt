package com.chaatyvideo.navyblue.pink.purple.chat.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.material.card.MaterialCardView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.chaatyvideo.navyblue.pink.purple.chat.R
import com.videochat.letsmeetvideochat.BaseClass

class GenderActivity : BaseClass() {
    lateinit var maleradiobtncard:MaterialCardView
    lateinit var femaleradiobtncard:MaterialCardView
    lateinit var maleimg:ImageView
    lateinit var femaleimg:ImageView
    lateinit var nextbtn:Button
    lateinit var backbtn:ImageView
    var perviousIteam:ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)

        adContainer = findViewById(R.id.banner_container)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)
        initLoad()
        getAdDataApi()

        maleradiobtncard = findViewById(R.id.malecard)
        femaleradiobtncard = findViewById(R.id.femalecard)
        maleimg = findViewById(R.id.maleimg)
        femaleimg = findViewById(R.id.femaleimg)
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
           deselectPerivousItem(femaleimg)
        }

        maleradiobtncard.setOnClickListener {
            deselectPerivousItem(maleimg)
        }

    }

    fun deselectPerivousItem(img: ImageView){
        perviousIteam?.isSelected = false
        img.isSelected = true
        perviousIteam = img
//        selected = false
    }
}