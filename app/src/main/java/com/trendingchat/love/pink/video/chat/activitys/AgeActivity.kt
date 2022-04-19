package com.trendingchat.love.pink.video.chat.activitys

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.trendingchat.love.pink.video.chat.R
import com.videochat.letsmeetvideochat.BaseClass

class AgeActivity : BaseClass() {
    lateinit var btnNext: Button
    lateinit var exitBtn: Button
    lateinit var hyperlink: TextView
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


        hyperlink = findViewById(R.id.tvDescription)

        val text = hyperlink.text

        val ss = SpannableString(text)

        val scanable = object : ClickableSpan(){
            override fun onClick(p0: View) {
                var policyUrl = getString(R.string.privacy_url)
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl))
                startActivity(browserIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.GREEN
            }
        }

        val scannable2 = object : ClickableSpan(){
            override fun onClick(widget: View) {
                var policyUrl = getString(R.string.tearms_use)
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl))
                startActivity(browserIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.GREEN
            }
        }

        ss.setSpan(scanable,41,55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(scannable2,60,70, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        hyperlink.text = ss
        hyperlink.movementMethod = LinkMovementMethod.getInstance()
    }
}