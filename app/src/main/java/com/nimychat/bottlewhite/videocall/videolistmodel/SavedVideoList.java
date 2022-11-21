package com.nimychat.bottlewhite.videocall.videolistmodel;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

public class SavedVideoList {

   public static void  saveVideos(Context context , VideoList list){
        SharedPreferences sharedPreferences =context.getSharedPreferences("videolist", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("task list", json);
        editor.apply();
       Log.d("DEEP","Video list Data is saved");
    }

  public static VideoList getVideos(Context context){
        VideoList list;
        SharedPreferences sharedPreferences =context.getSharedPreferences("videolist", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        list = gson.fromJson(json, VideoList.class);

        if (list == null) {
         return null;
        }
        return list;
    }

}
