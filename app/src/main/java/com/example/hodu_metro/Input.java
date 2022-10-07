package com.example.hodu_metro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

// w 평일, a 토요일, u 일요일
// hh시 mm분

import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//https://github.com/chrisbanes/PhotoView 라이브러리 가져와서씀
public class Input extends AppCompatActivity {
    static String departure_text;
    static String arrival_text;
    //static String formattedNow; //시간
    static String formattedH; //시
    static String formattedM;  //분
    static String week; //요일

    String text;
    static int count = 0;

    private long pressedTime;

    ProgressDialog customProgressDialog; //로딩창 구현 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        EditText departure = (EditText) findViewById(R.id.Edit1);// 출발역 입력칸
        EditText arrival = (EditText) findViewById(R.id.Edit2); // 도착역 입력칸

        Intent intent = getIntent();
        intent = getIntent();
        text = intent.getStringExtra("selected_item"); //search 액티비티에서 역이름 받아오기

        if (count == 1) { //출발역
            departure_text = text;
            departure.setText(departure_text);
            count = 0;
            arrival.setText(arrival_text);
        } else if (count == 2) { //도착역
            arrival_text = text;
            departure.setText(departure_text);
            arrival.setText(arrival_text);
            count = 0;
        }


        ////////////////////////////////////////////////////////////////////////////////

        // 이미지 줌인 줌 아웃
        PhotoView photoView = findViewById(R.id.photoView);
        photoView.setImageResource(R.drawable.map2);

        // 출발역 텍스트박스 클릭시 액티비티 전환
        departure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 1;
                Intent intent = new Intent(getApplicationContext(), Search.class); //search 액티비티로 전환
                startActivity(intent);
            }
        });

        // 도착역 텍스트박스 클릭시 액티비티 전환
        arrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 2;
                Intent intent = new Intent(getApplicationContext(), Search.class); //search 액티비티로 전환
                startActivity(intent);
            }
        });

        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //로딩창을 투명하게
        //customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        //검색 버튼 클릭시 액티비티 전환
        ImageButton search_button = (ImageButton) findViewById(R.id.button1);
        search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               /* try{
                    File file = new File("/storage/emulated/0/Download/Path3.json");
                    if(file.exists()){
                        file.delete();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }*/

                Date currentDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                int e = calendar.get(Calendar.DAY_OF_WEEK);

                //시간
                Date now = new Date();
                Date now1 = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("HH");
                SimpleDateFormat formatter1 = new SimpleDateFormat("mm");
                formattedH = formatter.format(now);
                formattedM = formatter1.format(now1);

                if (e == 2 || e == 3 || e == 4 || e == 5 || e == 6)
                    week = "W";
                else if (e == 7)
                    week = "A";
                else week = "U";

                Log.d("출발역", "출발역: " + departure_text);
                Log.d("도착역", "도착역: " + arrival_text);
                Log.d("현재시간", "now: " + formattedH);
                Log.d("현재분", "now: " + formattedM);
                Log.d("현재시간날짜", "요일: " + week);

                //Toast.makeText(getApplicationContext(), "짧게 출력 Hello World!", Toast.LENGTH_SHORT).show();
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileWriter writer = null;
                        try {
                            StringBuffer sb = new StringBuffer();
                            StringBuilder sb1 = new StringBuilder();
                            sb1.append("http://hodometro.iptime.org:8080/navi/?departure=");
                            sb1.append(departure_text);
                            sb1.append("&arrival=");
                            sb1.append(arrival_text);
                            sb1.append("&hour=");
                            sb1.append(formattedH);
                            sb1.append("&minute=");
                            sb1.append(formattedM);
                            sb1.append("&week=");
                            sb1.append(week);

                            URL url = new URL(sb1.toString());
                            //URL url = new URL("http://hodometro.iptime.org:8080/navi/?departure=천안&arrival=신창&hour=15&minute=53&week=W");

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            // 저 경로의 source를 받아온다.
                            if (conn != null) {
                                conn.setConnectTimeout(5000);
                                conn.setUseCaches(false);

                                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                                    while (true) {
                                        String line = br.readLine();
                                        if (line == null)
                                            break;
                                        sb.append(line + "\n");
                                    }
                                    Log.d("mylog", sb.toString());
                                    br.close();
                                }
                                conn.disconnect();
                            }

                            // 받아온 source를 JSONObject로 변환한다.
                            /*JSONObject jsonObj = new JSONObject(sb.toString());
                            JsonParser parser = new JsonParser();
                            JsonObject j = parser.parse(String.valueOf(jsonObj)).getAsJsonObject();
                            //Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            Gson gson = new GsonBuilder().create();
                            String json = gson.toJson(jsonObj);*/

                            /*JSONObject jsonObj = new JSONObject(sb.toString());
                            Gson gson = new GsonBuilder().create();
                            String json = gson.toJson(jsonObj);*/

                            JSONObject jsonObj = new JSONObject(sb.toString());
                            String json = jsonObj.toString();

                            Log.d("mylog2", json);


                            try {
                                writer = new FileWriter("/storage/emulated/0/Download/Path3.json",false);
                                writer.write(json);
                                Intent intent = new Intent(getApplicationContext(), RouteTime.class); //루트타임 페이지 호출
                                startActivity(intent);
                                finish();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                try {
                                    writer.close();
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                            Log.e("error", e.getMessage());
                        }
                    }
                });

                th.start();

                customProgressDialog.setMessage("Loading...");
                customProgressDialog.show(); //로딩 다이얼로그


                //버튼 클릭하면 현재시간, 요일 구함
                /*Intent intent = new Intent(getApplicationContext(), RouteTime.class); //루트타임 페이지 호출
                startActivity(intent);*/
                //////////////////////json 파싱/////////////////////////////////

/*                ArrayList List =new ArrayList();
                List.add(0,departure_text);
                List.add(1,arrival_text);
                List.add(2, formattedNow);
                List.add(3,week);*/

                /*JSONObject obj = new JSONObject();
                try {
                    obj.put("departure", departure_text);
                    obj.put("arrival", arrival_text);
                    obj.put("h", formattedH);
                    obj.put("m", formattedM);
                    obj.put("week", week);
                    Log.d("json확인", "확인: " + obj.toString());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }*/


            }
        });

    }

    @Override public void onBackPressed() {

        super.onBackPressed();

        ActivityCompat.finishAffinity(this); // 액티비티를 종료하고
        System.exit(0); // 프로세스를 종료

    }

}