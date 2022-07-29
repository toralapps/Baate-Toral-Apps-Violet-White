package com.lovechat.maroon.white.live.vodeo.call.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.lovechat.maroon.white.live.vodeo.call.MainActivity
import com.lovechat.maroon.white.live.vodeo.call.R
import com.lovechat.maroon.white.live.vodeo.call.singletons.ListOfVideos
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingScreen : AppCompatActivity() {
    lateinit var userimg:CircleImageView
    lateinit var userName:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)

        userimg = findViewById(R.id.circleImageView)
        userName = findViewById(R.id.username)

        Glide.with(userimg).load(ListOfVideos.videos?.ThumbnailUrl).placeholder(R.drawable.ic_person_pin)
            .error(R.drawable.ic_person_pin).into(userimg)

        userName.text = ListOfVideos.videos?.FirstName ?: "Judy Martin"

        lifecycleScope.launch {
            delay(3000)
            val intent = Intent(this@LoadingScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}