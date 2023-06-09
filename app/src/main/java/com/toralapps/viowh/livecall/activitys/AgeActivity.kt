package com.toralapps.viowh.livecall.activitys

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
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.app.ads.AdsViewModel
import com.app.ads.NewAddsActivty
import com.app.ads.utils.AdsState
import com.toralapps.viowh.livecall.R
import com.toralapps.viowh.livecall.livevideocall.util.CodeDecrypt
import com.toralapps.viowh.livecall.livevideocall.util.Config_Var
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import rateUs
import shareApp

@AndroidEntryPoint
class AgeActivity : NewAddsActivty() {
    val adsViewModel : AdsViewModel by viewModels()
    lateinit var btnNext: ImageView
    lateinit var hyperlink: TextView
    lateinit var loading_icon: LottieAnimationView
    lateinit var backbutton:ImageView
    lateinit var shareApp:ImageView
    lateinit var rateApp:ImageView
    lateinit var privecyPolicy:ImageView

    override val adContainer: LinearLayout?
        get() = findViewById(R.id.banner_container)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age)


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                adsViewModel.adsState.collect{adsState ->
                    when(adsState){
                        is AdsState.AdOpened ->{

                        }

                        is AdsState.AdClosed ->{
                           Intent(this@AgeActivity, GenderActivity::class.java).apply {
                               startActivity(this)
                           }
                        }

                        is AdsState.AdReady ->{

                        }
                    }
                }
            }
        }

        apiCall()

        loading_icon = findViewById(R.id.loading_icon)
        btnNext = findViewById(R.id.filledButton)
        backbutton = findViewById(R.id.backbtn)
        shareApp = findViewById(R.id.shareapp)
        rateApp = findViewById(R.id.rateapp)
        privecyPolicy = findViewById(R.id.privecypolicy)


        shareApp.setOnClickListener {
            shareApp()
        }

        rateApp.setOnClickListener {
            rateUs()
        }

        privecyPolicy.setOnClickListener {
            val policyUrl = getString(R.string.privacy_url)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl))
            startActivity(browserIntent)
        }

        backbutton.setOnClickListener {
            super.onBackPressed()
        }

        btnNext.setOnClickListener {
//            btnNext.visibility = View.INVISIBLE
//            loading_icon.visibility = View.VISIBLE
//            loading_icon.playAnimation()
            showIntertisialAdd()
        }

        loading_icon.addAnimatorListener(object :Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                loading_icon.visibility = View.INVISIBLE
                btnNext.visibility = View.VISIBLE
                    showIntertisialAdd()
            }


            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })

        hyperlink = findViewById(R.id.tvDescription)

        val text = hyperlink.text

        val ss = SpannableString(text)

        val scanable = object : ClickableSpan(){
            override fun onClick(p0: View) {
                val policyUrl = getString(R.string.privacy_url)
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl))
                startActivity(browserIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.RED
            }
        }

        val scannable2 = object : ClickableSpan(){
            override fun onClick(widget: View) {
                val policyUrl = getString(R.string.tearms_use)
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl))
                startActivity(browserIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.RED
            }
        }

        ss.setSpan(scanable,41,55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(scannable2,60,70, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        hyperlink.text = ss
        hyperlink.movementMethod = LinkMovementMethod.getInstance()
    }

    fun apiCall(){
        val jSONObject = JSONObject()
        try {
            jSONObject.put("version", 8)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val newRequestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(1, Config_Var.app_config, jSONObject,
            { jSONObject2 ->
                // from class: com.app.videocallrandomchat2.Splash.2
                try {
                    Log.d("DEEP","VolleyRespones")
                    Log.d("DEEP",jSONObject2.toString())
                    val jSONObject3 =  JSONObject(String(encrypt("^#$^$%^&(*&(*^*&^%$$)675", StringBuffer(StringBuffer(
                        StringBuffer(JSONObject(jSONObject2.toString()).optString("id")).reverse()
                            .toString().trim ()).reverse().toString()
                        .trim ()).reverse().toString())))


                    Log.d("DEEPjSON3",jSONObject3.toString())
                    Config_Var.user = jSONObject3.getString("user")
                    Log.d("DEEPconfig_user", Config_Var.user.toString())
                    Config_Var.pass = jSONObject3.getString("pass")
                    Config_Var.wsUrl = jSONObject3.getString("wsRoom")
                    Config_Var.turn = jSONObject3.getString("turn")
                    Config_Var.limit = jSONObject3.getInt("limit")
                    Config_Var.percent = jSONObject3.getInt("percent")
                    Config_Var.video_call = jSONObject3.getBoolean("video_call")

                } catch (e2: JSONException) {
                    e2.printStackTrace()
                }
            }) {
            Log.d("DEEP","ERROR Volley ${it.localizedMessage?.toString()}")
            //                progressBar.setVisibility(View.INVISIBLE);
            //                internet.setVisibility(View.VISIBLE);
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(30000, 1, 1.0f)
        newRequestQueue.cache.clear()
        newRequestQueue.add(jsonObjectRequest)
    }

    fun encrypt(str: String, str2: String): ByteArray {
        val decrypt = CodeDecrypt()
        return decrypt.encrypt(str,str2)
    }


    override fun onResume() {
        Log.d("DEEP","AgeActivity sub class onResume called")
        super.onResume()
    }
}