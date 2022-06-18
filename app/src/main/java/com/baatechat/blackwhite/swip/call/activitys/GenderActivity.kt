package com.baatechat.blackwhite.swip.call.activitys

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.baatechat.blackwhite.swip.call.R
import de.hdodenhof.circleimageview.CircleImageView

class GenderActivity : BaseClass() {
    lateinit var maleradiobtncard:ImageView
    lateinit var femaleradiobtncard:ImageView
    lateinit var nextbtn:CircleImageView
    lateinit var loading_icon: LottieAnimationView
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
            loading_icon = findViewById(R.id.loading_icon)


            backbtn.setOnClickListener {
                super.onBackPressed()
            }

            nextbtn.setOnClickListener {
                loading_icon.playAnimation()
                loading_icon.visibility = View.VISIBLE
                nextbtn.visibility = View.GONE
            }

            loading_icon.addAnimatorListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    showAddcounter = true
                    val intent = Intent(this@GenderActivity, LocationActivity::class.java)
                    startActivity(intent)
                    loading_icon.visibility = View.GONE
                    nextbtn.visibility = View.VISIBLE
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {

                }

            })

            femaleradiobtncard.setOnClickListener {
                femaleradiobtncard.setImageResource(R.drawable.female_selected)
                maleradiobtncard.setImageResource(R.drawable.male_not_selected)
            }

            maleradiobtncard.setOnClickListener {
                maleradiobtncard.setImageResource(R.drawable.male_selected)
                femaleradiobtncard.setImageResource(R.drawable.female_not_selected)
            }

    }

    fun deselectPerivousItem(img: ImageView){
        perviousIteam?.isSelected = false
        img.isSelected = true
        perviousIteam = img
//        selected = false
    }

    override fun onResume() {
        super.onResume()
        initShow()
    }
}