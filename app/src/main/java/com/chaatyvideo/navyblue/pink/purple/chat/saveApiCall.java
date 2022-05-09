package com.chaatyvideo.navyblue.pink.purple.chat;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.chaatyvideo.navyblue.pink.purple.chat.Interface.Data;

import java.lang.reflect.Type;
import java.util.ArrayList;

public  class saveApiCall {

   public static void  saveApi(Context context , ArrayList<Data> list){
        SharedPreferences sharedPreferences =context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("task list", json);
        editor.apply();
       Log.d("DEEP","Data is saved");
    }

  public static ArrayList<Data> getAds(Context context){
        ArrayList<Data> list = new ArrayList<>();
        SharedPreferences sharedPreferences =context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<Data>>() {}.getType();
        list = gson.fromJson(json, type);

        if (list == null) {
         ArrayList<Data> list2 = new ArrayList<>();
         return list2;
        }
        return list;
    }

}
