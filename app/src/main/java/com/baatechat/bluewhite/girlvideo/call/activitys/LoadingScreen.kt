package com.baatechat.bluewhite.girlvideo.call.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.baatechat.bluewhite.girlvideo.call.MainActivity
import com.baatechat.bluewhite.girlvideo.call.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)

        lifecycleScope.launch {
            delay(3000)
            val intent = Intent(this@LoadingScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}