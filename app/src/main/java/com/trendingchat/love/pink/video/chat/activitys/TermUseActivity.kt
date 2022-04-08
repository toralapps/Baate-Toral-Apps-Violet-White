package com.trendingchat.love.pink.video.chat.activitys

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.trendingchat.love.pink.video.chat.R

class TermUseActivity : AppCompatActivity() {
    lateinit var chk_share: CheckBox
    lateinit var chk_age: CheckBox
    lateinit var wv: WebView
    lateinit var txt_start: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term_use)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        chk_share = findViewById(R.id.chk_share);
        chk_age = findViewById(R.id.chk_age);
        txt_start=findViewById(R.id.txt_start);
        wv = findViewById(R.id.wv)as WebView

        wv.loadUrl("https://trendingchatapps.github.io/terms-of-use.html")
        wv.settings.javaScriptEnabled = true

        /*  if (chk_share.isChecked) {
              chk_share.setTextColor(resources.getColor(R.color.hc_black))
          } else {
              chk_share.setTextColor(resources.getColor(R.color.hc_red))

          }

          if (chk_age.isChecked) {
              chk_age.setTextColor(resources.getColor(R.color.hc_black))

          } else {
              chk_age.setTextColor(resources.getColor(R.color.hc_red))

          }
  */
        chk_share.setOnCheckedChangeListener { compoundButton, b ->

            if (chk_share.isChecked) {
                chk_share.setTextColor(resources.getColor(R.color.black))

            } else {
                chk_share.setTextColor(resources.getColor(R.color.purple_200))


            }
        }
        chk_age.setOnCheckedChangeListener { compoundButton, b ->

            if (chk_age.isChecked) {
                chk_age.setTextColor(resources.getColor(R.color.black))

            } else {
                chk_age.setTextColor(resources.getColor(R.color.purple_200))


            }
        }

        txt_start.setOnClickListener {
            if (!chk_age.isChecked) {

                chk_age.setTextColor(resources.getColor(R.color.purple_200))


            }
            if (!chk_share.isChecked) {

                chk_share.setTextColor(resources.getColor(R.color.purple_200))


            }


            if (chk_share.isChecked && chk_age.isChecked) {
                getSharedPreferences("TERMPREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();
                val intent = Intent(this@TermUseActivity, AgeActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
            {
                Toast.makeText(this,"Please Check Use Of Term", Toast.LENGTH_LONG).show()
            }


        }

    }
}