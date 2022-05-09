package com.chaatyvideo.navyblue.pink.purple.chat.activitys

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.chaatyvideo.navyblue.pink.purple.chat.R
import com.chaatyvideo.navyblue.pink.purple.chat.extenstionfunctions.snackBar

class TermUseActivity : AppCompatActivity() {
    lateinit var wv: WebView
    lateinit var declinebtn:TextView
    lateinit var acceptbtn:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_terms_use_layout)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        declinebtn = findViewById(R.id.declain);
        acceptbtn = findViewById(R.id.accept);
        wv = findViewById(R.id.wv)as WebView

        wv.loadUrl("https://chaatyvideoapps.github.io/terms-of-use.html")
        wv.settings.javaScriptEnabled = true


        acceptbtn.setOnClickListener {
                getSharedPreferences("TERMPREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();
                val intent = Intent(this@TermUseActivity, AgeActivity::class.java)
                startActivity(intent)
                finish()
            }

        declinebtn.setOnClickListener {
            snackBar(wv,"You need to Accept terms and condition to continue")
        }

        }

    }