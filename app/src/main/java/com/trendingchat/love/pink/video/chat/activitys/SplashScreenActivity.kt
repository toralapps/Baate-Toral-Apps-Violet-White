package com.trendingchat.love.pink.video.chat.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.trendingchat.love.pink.video.chat.MainActivity
import com.trendingchat.love.pink.video.chat.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val isFirstRun = getSharedPreferences("TERMPREFERENCE", MODE_PRIVATE)
            .getBoolean("isFirstRun", true)

        if (isFirstRun) {
            lifecycleScope.launchWhenCreated {
                delay(3000)
                val intent = Intent(this@SplashScreenActivity, TermUseActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            lifecycleScope.launchWhenCreated {
                delay(3000)
                val intent = Intent(this@SplashScreenActivity,AgeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}