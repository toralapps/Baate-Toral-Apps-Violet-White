package com.chaatyvideo.green.black.romantic.chat.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.chaatyvideo.green.black.romantic.chat.R
import kotlinx.coroutines.delay

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_splash_screen_layout)

        val isFirstRun = getSharedPreferences("TERMPREFERENCE", MODE_PRIVATE)
            .getBoolean("isFirstRun", true)

        if (isFirstRun) {
            lifecycleScope.launchWhenCreated {
                delay(2000)
                val intent = Intent(this@SplashScreenActivity, TermUseActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            lifecycleScope.launchWhenCreated {
                delay(2000)
                val intent = Intent(this@SplashScreenActivity,AgeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}