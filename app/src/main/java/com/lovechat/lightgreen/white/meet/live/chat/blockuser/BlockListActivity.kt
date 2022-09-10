package com.lovechat.lightgreen.white.meet.live.chat.blockuser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.myech.video.bluepink.chat.blockuser.BlockList
import com.lovechat.lightgreen.white.meet.live.chat.R
import com.lovechat.lightgreen.white.meet.live.chat.videolistmodel.Data


class BlockListActivity : AppCompatActivity(), BlockListAdapter.Interaction {
    lateinit var recyclerView:RecyclerView
    lateinit var emptytext:LottieAnimationView
    lateinit var adapter: BlockListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_list)

        emptytext = findViewById(R.id.emptytext)
        recyclerView = findViewById(R.id.recyclerview)

        if (BlockList.getBlockVideos(this).isEmpty()){
            recyclerView.visibility = View.GONE
            emptytext.visibility = View.VISIBLE
        }else {
            adapter = BlockListAdapter(this@BlockListActivity)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            adapter.submitList(BlockList.getBlockVideos(this))
        }
    }

    override fun onItemSelected(position: Int, item: Data) {
        Log.d("DEEP","unblock")
        Log.d("DEEP","${position.toString()} is clicked")
        val list = BlockList.getBlockVideos(this)
        if (list.isNotEmpty()){
            Log.d("DEEP",list.size.toString())
            list.removeAt(position)
            Log.d("DEEP",list.size.toString())
            BlockList.saveBlockVideos(this, list)
            if(list.isEmpty()){
                emptytext.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            adapter.submitList(list)
            Snackbar.make(emptytext,"User has been unblocked",Snackbar.LENGTH_SHORT).show()
        }
    }
}