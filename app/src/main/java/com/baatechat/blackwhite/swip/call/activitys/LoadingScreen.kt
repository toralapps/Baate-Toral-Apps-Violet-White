package com.baatechat.blackwhite.swip.call.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.baatechat.blackwhite.swip.call.MainActivity
import com.baatechat.blackwhite.swip.call.R
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