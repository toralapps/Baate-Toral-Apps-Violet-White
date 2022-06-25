package com.photography.hindishayari.activitys

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.photography.hindishayari.R
import de.hdodenhof.circleimageview.CircleImageView
import shareApp

class AgeActivity : BaseClass() {
    lateinit var btnNext: CircleImageView
    lateinit var hyperlink: TextView
    lateinit var loading_icon: LottieAnimationView
    lateinit var backbutton:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age)

        adContainer = findViewById(R.id.banner_container)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)


        loading_icon = findViewById(R.id.loading_icon)
        btnNext = findViewById(R.id.filledButton)
        backbutton = findViewById(R.id.backbtn)


        backbutton.setOnClickListener {
            super.onBackPressed()
        }

        btnNext.setOnClickListener {
            loading_icon.playAnimation()
            loading_icon.visibility = View.VISIBLE
            btnNext.visibility = View.GONE
        }

        loading_icon.addAnimatorListener(object :Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                    val intent = Intent(this@AgeActivity, GenderActivity::class.java)
                    startActivity(intent)
                    loading_icon.visibility = View.GONE
                    btnNext.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }

        })

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