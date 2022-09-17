package com.example.hodu_metro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
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

//https://github.com/chrisbanes/PhotoView 라이브러리 가져와서씀
public class Input extends AppCompatActivity {
    static String departure_text;
    static String arrival_text;
    static String formattedNow; //시간
    static String week; //요일

    String text;
    static int count = 0;

    private long pressedTime;


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

        //검색 버튼 클릭시 액티비티 전환
        ImageButton search_button = (ImageButton) findViewById(R.id.button1);
        search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //버튼 클릭하면 현재시간, 요일 구함
                Date currentDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                int e = calendar.get(Calendar.DAY_OF_WEEK);

                //시간
                Date now = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("HH시 mm분");
                formattedNow = formatter.format(now);

                if (e == 2 || e == 3 || e == 4 || e == 5 || e == 6)
                    week = "w";
                else if (e == 7)
                    week = "a";
                else week = "u";

                Log.d("출발역", "출발역: " + departure_text);
                Log.d("도착역", "도착역: " + arrival_text);
                Log.d("현재시간날짜", "now: " + formattedNow);
                Log.d("현재시간날짜", "요일: " + week);


                //////////////////////json 파싱/////////////////////////////////

                JSONObject obj = new JSONObject();
                try {
                    obj.put("departure", departure_text);
                    obj.put("arrival", arrival_text);
                    obj.put("now", formattedNow);
                    obj.put("week", week);

                    Log.d("json확인", "확인: " + obj.toString());

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "짧게 출력 Hello World!", Toast.LENGTH_SHORT).show();
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileWriter writer = null;
                        try {
                            StringBuffer sb = new StringBuffer();
                            URL url = new URL("http://172.30.4.105:8080/navi?departure=천안&arrival=신창&hour=15&minute=53&week=W");

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
                                    Log.d("myLog", sb.toString());
                                    br.close();
                                }
                                conn.disconnect();
                            }

                            // 받아온 source를 JSONObject로 변환한다.
                            //JSONObject jsonObj = new JSONObject(sb.toString());

                            Jsonobj.jsonObject = new JSONObject(sb.toString());
                            Jsonobj.init();
                            Log.d("json파일확인 " ,"확인"+ Jsonobj.jsonObject);

                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            //String json = gson.toJson(jsonObj);
                            /*try {
                                writer = new FileWriter("/storage/emulated/0/Download/Path.json");
                                writer.write(json);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                try {
                                    writer.close();
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }*/

                        } catch (Exception e){
                            e.printStackTrace();
                            Log.e("error", e.getMessage());
                        }
                    }
                });

                th.start();

                Intent intent = new Intent(getApplicationContext(), RouteTime.class); //루트타임 페이지 호출
                startActivity(intent);
            }
        });

    }

    @Override public void onBackPressed() {

        super.onBackPressed();

        ActivityCompat.finishAffinity(this); // 액티비티를 종료하고
        System.exit(0); // 프로세스를 종료

    }

}
