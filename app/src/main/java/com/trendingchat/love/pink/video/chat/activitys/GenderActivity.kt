package com.trendingchat.love.pink.video.chat.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.material.card.MaterialCardView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.trendingchat.love.pink.video.chat.R
import com.videochat.letsmeetvideochat.BaseClass

class GenderActivity : BaseClass() {
    lateinit var maleradiobtn:MaterialRadioButton
    lateinit var maleradiobtncard:MaterialCardView
    lateinit var femaleradiobtncard:MaterialCardView
    lateinit var femaleradiobtn:MaterialRadioButton
    lateinit var nextbtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)

        adContainer = findViewById(R.id.banner_container)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)
        initLoad()
        getAdDataApi()

        maleradiobtn = findViewById(R.id.maleradiobtn)
        femaleradiobtn = findViewById(R.id.femaleradiobtn)
        maleradiobtncard = findViewById(R.id.malecard)
        femaleradiobtncard = findViewById(R.id.femalecard)

        nextbtn = findViewById(R.id.elevatedButton)

        nextbtn.setOnClickListener {
            val intent = Intent(this,ChatRoomActivity::class.java)
            startActivity(intent)
        }

        femaleradiobtncard.setOnClickListener {
            femaleradiobtn.isChecked = true
        }

        maleradiobtncard.setOnClickListener {

            maleradiobtn.isChecked = true
        }

        maleradiobtn.setOnCheckedChangeListener { compoundButton, b ->
            femaleradiobtn.isChecked = !b
        }

        femaleradiobtn.setOnCheckedChangeListener { compoundButton, b ->
            maleradiobtn.isChecked = !b
        }
    }
}