package com.myech.video.bluepink.chat.blockuser

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nimychat.bottlewhite.videocall.videolistmodel.Data

class BlockList {

    companion object {

        fun saveBlockVideos(context: Context, list: List<Data>) {
            val sharedPreferences =
                context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(list)
            editor.putString("Block list", json)
            editor.apply()
            Log.d("DEEP", "Data is saved")

        }

        fun getBlockVideos(context: Context): ArrayList<Data> {
            var list: MutableList<Data>? = mutableListOf()
            val sharedPreferences =
                context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString("Block list", null)
            val type = object : TypeToken<ArrayList<Data?>?>() {}.type
            list = gson.fromJson<ArrayList<Data>>(json, type)
            return (list ?: ArrayList()) as ArrayList<Data>
        }
    }
}