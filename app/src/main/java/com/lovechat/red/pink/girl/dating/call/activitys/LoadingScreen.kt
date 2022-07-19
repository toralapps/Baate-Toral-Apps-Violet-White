package com.lovechat.red.pink.girl.dating.call.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.lovechat.red.pink.girl.dating.call.MainActivity
import com.lovechat.red.pink.girl.dating.call.R
import com.lovechat.red.pink.girl.dating.call.singletons.ListOfVideos
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingScreen : AppCompatActivity() {
    lateinit var userimg:CircleImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)

        userimg = findViewById(R.id.circleImageView)

        Glide.with(userimg).load(ListOfVideos.videos?.ThumbnailUrl).placeholder(R.drawable.ic_person_pin)
            .error(R.drawable.ic_person_pin).into(userimg)

        lifecycleScope.launch {
            delay(3000)
            val intent = Intent(this@LoadingScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}