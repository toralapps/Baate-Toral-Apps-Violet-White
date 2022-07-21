package com.app.ads.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.app.ads.domain.models.Data;

public class SavedAds {

   public static void  saveApi(Context context , Data list){
        SharedPreferences sharedPreferences =context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("task list", json);
        editor.apply();
       Log.d("DEEP","Data is saved");
    }

  public static Data getAds(Context context){
        Data list;
        SharedPreferences sharedPreferences =context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        list = gson.fromJson(json, Data.class);

        if (list == null) {
         return null;
        }
        return list;
    }

}
