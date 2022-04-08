package com.trendingchat.love.pink.video.chat.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.trendingchat.love.pink.video.chat.R
import com.videochat.letsmeetvideochat.BaseClass

class AgeActivity : BaseClass() {
    lateinit var btnNext: Button
    lateinit var exitBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age)


        adContainer = findViewById(R.id.banner_container)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)
        initLoad()
        getAdDataApi()

        btnNext = findViewById(R.id.filledButton)
        exitBtn = findViewById(R.id.exitbtn)

        btnNext.setOnClickListener {
            val intent = Intent(this,GenderActivity::class.java)
            startActivity(intent)
        }

        exitBtn.setOnClickListener {
            finish()
        }
    }
}