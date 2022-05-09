package com.chaatyvideo.navyblue.pink.purple.chat.activitys

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.chaatyvideo.navyblue.pink.purple.chat.R
import com.chaatyvideo.navyblue.pink.purple.chat.adapter.AgeSelectorAdapter
import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.AgeSelection
import com.google.android.material.button.MaterialButton
import com.shawnlin.numberpicker.NumberPicker
import com.videochat.letsmeetvideochat.BaseClass

class AgeActivity : BaseClass() {
    lateinit var btnNext: TextView
    lateinit var exitBtn: MaterialButton
    lateinit var hyperlink: TextView
    lateinit var nuberPicker: NumberPicker
    var selectedvalue:Int = 18
    lateinit var _navigationList: MutableLiveData<List<AgeSelection>>
    val listOfNumber = mutableListOf<AgeSelection>()
    var positon:Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age)

        adContainer = findViewById(R.id.banner_container)
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)
        initLoad()
        getAdDataApi()


        nuberPicker = findViewById(R.id.number_picker)
        val listOfNumbers= mutableListOf<String>()

        for (i in 18..81){
            listOfNumbers.add(i.toString())
        }

        nuberPicker.let {
            it.minValue =1
            it.maxValue =listOfNumbers.size
            it.displayedValues = listOfNumbers.toTypedArray()
            it.value =1
        }

        nuberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("deep",newVal.toString())
            selectedvalue = newVal+18
        }


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