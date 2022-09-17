package com.example.hodu_metro;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;

public  class Jsonobj implements Serializable {
    static JSONObject jsonObject;
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static String json;
    static public void init() {
        json = gson.toJson(jsonObject);
        Log.d("json파일확인obj " ,"확인"+ json);
    }

    static public String hello() {
        return json;
    }
}
