package com.example.hodu_metro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;

import android.view.View;
import android.graphics.Typeface;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;


public class RouteCongest extends AppCompatActivity {

    /*public static final String TAG = "MAIN";
    private TextView time_text;*/

    //뒤로가기 버튼 누르면 Input 액티비티로 이동
    @Override
    public void onBackPressed() {

        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(), Input.class));

    }

    //textview 선언
    TextView duration_textview; //소요시간
    TextView numStep_textview;  //경유역
    TextView transferNum_textview;  //환승 횟수

    //TextView info_textview;

    LinearLayout listView; // 레이아웃 객체 생성

    int numStep_ = 0;
    int[] countf = new int[100];
    int[] countl = new int[100];
    int[] countt = new int[100]; //급행
    String duration_t = "";
    String numStep_t = "";
    String transferNum_t = "";
    String[] hourminute_t = new String[100];
    String[] transferTime_t = new String[100];
    String[] staitonName_tt = new String[100];
    String[] scheduleName_t = new String[100];
    String[] lineId_t = new String[100];
    String[] congestScore_t = new String[100];
    String[] typeName_t = new String[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_congestion);

        //최단시간 버튼 클릭시 화면 전환
        TextView shortest_time=(TextView) findViewById(R.id.shortest_time);
        shortest_time.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteTime.class);
                startActivity(intent);
            }
        });

        //최소환승 버튼 클릭시 화면 전환
        TextView min_transfer=(TextView) findViewById(R.id.min_transfer);
        min_transfer.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteTransfer.class);
                startActivity(intent);
            }
        });
        listView = findViewById(R.id.listView);

        //xml 아이디 - textview 지정
        duration_textview = findViewById(R.id.duration);
        numStep_textview = findViewById(R.id.numStep);
        transferNum_textview = findViewById(R.id.transferNum);

        try{

            FileReader is = new FileReader("/storage/emulated/0/Download/Path3.json");

            BufferedReader reader = new BufferedReader(is);

            StringBuffer buffer = new StringBuffer();
            String line = reader.readLine();
            while(line != null) {
                buffer.append(line + "\n");
                line = reader.readLine();
            }


      /*  AssetManager assetManager = getAssets();
        //assets/ test.json 파일 읽기 위한 InputStream
        try {
            //InputStream is = assetManager.open("test.json"); //환승 1회
            //InputStream is = assetManager.open("test1.json"); //환승 2회
            InputStream is = assetManager.open("test2.json"); //환승 2회
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line + "\n");
                line = reader.readLine();
            }
*/

            String jsonData = buffer.toString();

            //json 데이터가 ShortestPath 일 경우
            JSONObject jsonObject = new JSONObject(jsonData);

            Log.d("오브젝트 비었는지 확인","11");

            //info_textview = findViewById(R.id.info);

            TextView info_textview = new TextView(getApplicationContext());

            if(jsonObject.isNull("LowCongestPath")){
                info_textview.setText("혼잡도 정보를\n지원하지 않는 경로입니다.");
                info_textview.setTextSize(18);
                info_textview.setGravity(Gravity.CENTER);
                //info_textview.setBackgroundColor(Color.rgb(255, 255, 0));
                listView.addView(info_textview);
            }

            LinearLayout.LayoutParams param8 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param8.topMargin = 350;
            param8.leftMargin=130;
            param8.width=700;
            param8.gravity=Gravity.CENTER;


            info_textview.setLayoutParams(param8);

            JSONArray jsonArray = jsonObject.getJSONArray("LowCongestPath");
            for (int i = 0; i < jsonArray.length(); i++) {

                //전체 역의 값을 갖고오는 object (전체, all)
                JSONObject a = jsonArray.getJSONObject(i);
                //첫번째 역의 값을 갖고오는 object (출발역, first)
                JSONObject f = jsonArray.getJSONObject(0);
                //마지막 역의 값을 갖고오는 object (도착역, last)
                JSONObject l = jsonArray.getJSONObject(jsonArray.length() - 1);


                String stationName_a = a.getString("stationName");
                String stationName_f = f.getString("stationName");
                String stationName_l = l.getString("stationName");

                JSONObject schedule_a = a.getJSONObject("schedule");
                JSONObject schedule_f = f.getJSONObject("schedule");
                JSONObject schedule_l = l.getJSONObject("schedule");

                JSONObject transfer_a = a.getJSONObject("transfer");
                JSONObject transfer_f = f.getJSONObject("transfer");
                JSONObject transfer_l = l.getJSONObject("transfer");

                //전체역 담는 배열
                staitonName_tt[i] = stationName_a;

                //전체역 환승 여부 체크
                Boolean isTransfer = transfer_a.getBoolean("isTransfer");
                int transferTime = transfer_a.getInt("transferTime");
                int transferNum = transfer_a.getInt("transferNum");

                //탑승 시간
                int hour = schedule_a.getInt("hour");
                int minute = schedule_a.getInt("minute");

                //방면
                String scheduleName = schedule_a.getString("scheduleName");
                scheduleName_t[i] = "<"+scheduleName + "행>";
                hourminute_t[i] = hour + ":" + minute;

                //호선
                String lineId = a.getString("lineId");
                lineId_t[i] = lineId;

                //혼잡도
                int congestScore = schedule_a.getInt("congestScore");
                String congestScore_s = Integer.toString(congestScore);
                congestScore_t[i] = congestScore_s;

                //급행
                //Arrays.fill(countt,99); //countt배열 전체 99로 초기화

                String typeName = schedule_a.getString("typeName");
                typeName_t[i] = typeName;
                Log.d("typename", typeName_t[i]); //typename확인

                if(typeName_t[i].equals("S")){
                    countt[i] = 99;
                }

                Log.d("countt", "count "+countt[i]);

                //환승이 true 인 역의 숫자 저장
                if (isTransfer == true) {
                    countl[i] = i;
                    if (transferNum == 0 && transferTime != 0 || transferNum == 1 && transferTime != 0 || transferNum == 2 && transferTime != 0 || transferNum == 3 && transferTime != 0 || transferNum == 4 && transferTime != 0 || transferNum == 5 && transferTime != 0 || transferNum == 6 && transferTime != 0 || transferNum == 7 && transferTime != 0) {
                        countf[i] = i;
                        if (transferTime < 60)
                            transferTime_t[i] = transferTime + "초";
                        else {
                            int minute_t = transferTime / 60;
                            int second_t = transferTime % 60;

                            if (second_t == 0) {
                                transferTime_t[i] = "도보 " + minute_t + "분";
                            } else {
                                transferTime_t[i] = "도보 " + minute_t + "분 " + second_t + "초";
                            }
                            //Log.d("transfer", transferTime_t[i]);
                        }
                    }
                }

                //상단 정보
                int duration_l = schedule_l.getInt("duration");  //소요시간
                int numStep_l = schedule_l.getInt("numStep");  //경유역
                int transferNum_l = transfer_l.getInt("transferNum");  //환승횟수

                if (transferNum_l == 0) {
                    numStep_ = numStep_l + 1;
                } else if (transferNum_l == 1) {
                    numStep_ = numStep_l + 2;
                }else if (transferNum_l == 2) {
                    numStep_ = numStep_l + 3;
                } else if (transferNum_l == 3) {
                    numStep_ = numStep_l + 4;
                } else if (transferNum_l == 4) {
                    numStep_ = numStep_l + 5;
                } else if (transferNum_l == 5) {
                    numStep_ = numStep_l + 6;
                } else if (transferNum_l == 6) {
                    numStep_ = numStep_l + 7;
                }

                //소요시간(시간,분 으로 변경)
                if (duration_l < 60)
                    duration_t = duration_l + "분";
                else {
                    int hour_d = duration_l / 60;
                    int minute_d = duration_l % 60;
                    duration_t = hour_d + "시간" + minute_d + "분";
                }

                //경유역 개수
                numStep_t = "경유역 " + numStep_l + "개";
                //환승 횟수
                transferNum_t = "환승" + transferNum_l + "회";

            }

            createBigView();

            //화면에 출력
            //소요시간, 경유역, 환승횟수
            duration_textview.setText(duration_t);
            numStep_textview.setText(numStep_t);
            transferNum_textview.setText(transferNum_t);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ResourceType")


    private void createBigView() {

        String SS="급행";

        for (int i = 0; i < numStep_; i++) {
            TextView textViewNm = new TextView(getApplicationContext()); //출발역, 도착역, 환승역
            TextView textViewTime = new TextView(getApplicationContext()); //열차시간
            TextView textViewTransfer = new TextView(getApplicationContext()); //도보 이동 시간
            TextView textViewscheduleName = new TextView(getApplicationContext()); //방면
            TextView textViewlineId = new TextView(getApplicationContext());// 호선
            TextView textViewcongestScore = new TextView(getApplicationContext()); //혼잡도

            TextView T = new TextView(getApplicationContext()); //환승 표시 T
            TextView S = new TextView(getApplicationContext()); //급행표시 S
            textViewNm.setText(staitonName_tt[i]); //역이름 출력


            if (i == 0) { //출발역 처리

                if(countt[i]==99){ //급행처리
                    Log.d("카운트","카운트"+countt[i]);
                    textViewscheduleName.setText(scheduleName_t[i]+" "+SS); //방면
                    textViewscheduleName.setTextColor(Color.rgb(255, 0, 51));
                }
                else{
                    textViewscheduleName.setText(scheduleName_t[i]); //방면
                }

                textViewlineId.setText(lineId_t[i]);// 호선
                textViewlineId.setGravity(Gravity.CENTER);// 호선 가운데 정렬
                textViewlineId.setTypeface(null, Typeface.BOLD); //호선 글씨 굵게
                textViewTime.setText(hourminute_t[i]); //열차시간
                textViewTime.setTypeface(null, Typeface.BOLD); //열차시간 글씨 굵게
                //textViewscheduleName.setText(scheduleName_t[i]); //방면
                textViewcongestScore.setText(congestScore_t[i]); //혼잡도

                textViewscheduleName.setTextSize(13); // 방면 텍스트 크기 지정
                textViewTime.setTextSize(13);//열차시간 텍스트 크기 지정
                textViewNm.setTextSize(23); //출발역 텍스트 크기 지정

                //텍스트뷰와 레이아웃 연결
                listView.addView(textViewlineId);
                listView.addView(textViewTime);
                listView.addView(textViewcongestScore);
                listView.addView(textViewNm);
                listView.addView(textViewscheduleName);

            } else if (i == countf[i]) { //1번(위) 환승역 처리

                T.setText("환승");
                T.setGravity(Gravity.CENTER); //호선 가운데 정렬
                T.setTypeface(null, Typeface.BOLD); //호선 글씨 굵게
                textViewTime.setText(hourminute_t[i]);
                textViewTime.setTypeface(null, Typeface.BOLD); //열차시간 글씨 굵게
                textViewTransfer.setText(transferTime_t[i]);
                textViewcongestScore.setText(congestScore_t[i]); //혼잡도

                T.setTextSize(11); //환승표시 텍스트 크기
                textViewNm.setTextSize(23);//환승역(첫번째) 텍스트 크기 지정
                textViewTime.setTextSize(13);//열차시간 텍스트 크기 지정

                //텍스트뷰와 레이아웃 연결
                listView.addView(T);
                listView.addView(textViewTime);
                listView.addView(textViewcongestScore);
                listView.addView(textViewNm);
                listView.addView(textViewTransfer);

            } else if (i == countl[i]) { //2번(아래) 환승역 처리

                if(countt[i]==99){ //급행처리
                    Log.d("카운트","카운트"+countt[i]);
                    textViewscheduleName.setText(scheduleName_t[i]+" "+SS); //방면
                    textViewscheduleName.setTextColor(Color.rgb(255, 0, 51));
                }
                else{
                    textViewscheduleName.setText(scheduleName_t[i]); //방면
                }

                textViewlineId.setText(lineId_t[i]); //호선
                textViewlineId.setGravity(Gravity.CENTER); //호선 가운데 정렬
                textViewlineId.setTypeface(null, Typeface.BOLD); // 호선 글씨 굵게
                textViewTime.setText(hourminute_t[i]);
                textViewTime.setTypeface(null, Typeface.BOLD); //열차시간 글씨 굵게
                //textViewscheduleName.setText(scheduleName_t[i]);
                textViewcongestScore.setText(congestScore_t[i]); //혼잡도
                textViewscheduleName.setTextSize(13); // 방면 텍스트 크기 지정
                textViewNm.setTextSize(23);
                textViewTime.setTextSize(13);//열차시간 텍스트 크기 지정

                //텍스트뷰와 레이아웃 연결
                listView.addView(textViewlineId);
                listView.addView(textViewTime);
                listView.addView(textViewcongestScore);
                listView.addView(textViewNm);
                listView.addView(textViewscheduleName);


            } else if (i == numStep_ - 1) { //도착역 처리
                T.setText("도착");
                T.setGravity(Gravity.CENTER); // 가운데 정렬
                T.setTypeface(null, Typeface.BOLD); // 글씨 굵게
                T.setTextSize(11); //도착표시 텍스트 크기
                textViewTime.setText(hourminute_t[i]);
                textViewTime.setTypeface(null, Typeface.BOLD); //열차시간 글씨 굵게
                textViewTime.setTextSize(13);//열차시간 텍스트 크기 지정
                textViewNm.setTextSize(23);
                textViewcongestScore.setText(congestScore_t[i]); //혼잡도

                //텍스트뷰와 레이아웃 연결
                listView.addView(T);
                listView.addView(textViewTime);
                listView.addView(textViewcongestScore);
                listView.addView(textViewNm);

            }
            else { //경유역, 혼잡도
                textViewcongestScore.setText(congestScore_t[i]);//혼잡도

                textViewNm.setTextSize(15); //경유역 텍스트 크기

                //텍스트뷰와 레이아웃 연결
                listView.addView(textViewcongestScore);
                listView.addView(textViewNm);
            }

            // 텍스트뷰 글자타입 설정
            textViewNm.setTypeface(null, Typeface.BOLD);
            textViewNm.setTextColor(Color.rgb(0, 0, 0));
            textViewcongestScore.setTextSize(10);
            textViewTransfer.setTypeface(null, Typeface.BOLD);
            textViewTransfer.setTextColor(Color.GRAY);

            // 텍스트뷰 ID설정
            textViewNm.setId(i);
            textViewTransfer.setId(i);
            textViewTime.setId(i);
            textViewscheduleName.setId(i);
            textViewlineId.setId(i);


            // 레이아웃 설정
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param.leftMargin = 340; //출발,도착역
            param.bottomMargin = 100;
            param.topMargin = -65;

            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param1.leftMargin = 170; //열차시간
            param1.topMargin = -60;
            param1.bottomMargin = 0;

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param2.leftMargin = 340; //환승시간
            param2.bottomMargin = 80;
            param2.topMargin = -20;

            LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param3.leftMargin = 70; //호선
            //param3.bottomMargin = 10;
            //param3.topMargin = -60;
            param3.width = 80;
            param3.height = 80;

            LinearLayout.LayoutParams param4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param4.leftMargin = 340; //방면
            param4.bottomMargin = 120;
            param4.topMargin = -90;

            LinearLayout.LayoutParams param5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param5.leftMargin = 280; //혼잡도
            //param5.bottomMargin = -30;
            param5.topMargin = -45;
            param5.width = 40;
            param5.height = 40;

            LinearLayout.LayoutParams param6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param6.leftMargin = 70; //환승표시 T
            //param5.bottomMargin = -30;
            param6.topMargin = -60;
            param6.width = 80;
            param6.height = 80;

            LinearLayout.LayoutParams param7 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            //param7.leftMargin = 70; // 급행표시 S
            //param7.bottomMargin = -30;
            //param7.topMargin = -60;


            //설정한 레이아웃 텍스트뷰에 적용
            textViewNm.setLayoutParams(param);
            textViewTime.setLayoutParams(param1);
            textViewTransfer.setLayoutParams(param2);
            textViewlineId.setLayoutParams(param3);
            textViewscheduleName.setLayoutParams(param4);
            textViewcongestScore.setLayoutParams(param5);
            T.setLayoutParams(param6);
            S.setLayoutParams(param7);

            //텍스트뷰 백그라운드색상 설정
            textViewNm.setBackgroundColor(Color.rgb(255, 255, 255));
            textViewTransfer.setBackgroundColor(Color.rgb(255, 255, 255));
            textViewTime.setBackgroundColor(Color.rgb(255, 255, 255));
            textViewscheduleName.setBackgroundColor(Color.rgb(255, 255, 255));
            T.setTextColor(Color.rgb(255, 255, 255));
            textViewlineId.setTextColor((Color.rgb(255, 255, 255)));

            //혼잡도
            if (congestScore_t[i].equals("3")) {
                textViewcongestScore.setBackgroundColor(Color.RED);
                textViewcongestScore.setTextColor(Color.RED);
                textViewcongestScore.setBackgroundResource(R.drawable.round_red);
            } else if (congestScore_t[i].equals("2")) {
                textViewcongestScore.setBackgroundColor(Color.YELLOW);
                textViewcongestScore.setTextColor(Color.YELLOW);
                textViewcongestScore.setBackgroundResource(R.drawable.round_yellow);
            } else if (congestScore_t[i].equals("1")) {
                textViewcongestScore.setBackgroundColor(Color.GREEN);
                textViewcongestScore.setTextColor(Color.GREEN);
                textViewcongestScore.setBackgroundResource(R.drawable.round_green);
            } else if (congestScore_t[i].equals("0")) {
                textViewcongestScore.setTextColor(Color.rgb(204,204,204));
                textViewcongestScore.setBackgroundResource(R.drawable.round_white);
            }


            if (lineId_t[i].equals("1")) {
                T.setBackgroundResource(R.drawable.line_1);
                textViewlineId.setBackgroundResource(R.drawable.line_1);
            } else if (lineId_t[i].equals("2")) {
                T.setBackgroundResource(R.drawable.line_2);
                textViewlineId.setBackgroundResource(R.drawable.line_2);
            } else if (lineId_t[i].equals("3")) {
                T.setBackgroundResource(R.drawable.line_3);
                textViewlineId.setBackgroundResource(R.drawable.line_3);
            } else if (lineId_t[i].equals("4")) {
                T.setBackgroundResource(R.drawable.line_4);
                textViewlineId.setBackgroundResource(R.drawable.line_4);
            } else if (lineId_t[i].equals("5")) {
                T.setBackgroundResource(R.drawable.line_5);
                textViewlineId.setBackgroundResource(R.drawable.line_5);
            } else if (lineId_t[i].equals("6")) {
                T.setBackgroundResource(R.drawable.line_6);
                textViewlineId.setBackgroundResource(R.drawable.line_6);
            } else if (lineId_t[i].equals("7")) {
                T.setBackgroundResource(R.drawable.line_7);
                textViewlineId.setBackgroundResource(R.drawable.line_7);
            } else if (lineId_t[i].equals("8")) {
                T.setBackgroundResource(R.drawable.line_8);
                textViewlineId.setBackgroundResource(R.drawable.line_8);
            } else if (lineId_t[i].equals("9")) {
                T.setBackgroundResource(R.drawable.line_9);
                textViewlineId.setBackgroundResource(R.drawable.line_9);
            } else if (lineId_t[i].equals("21")) {//인천1호선
                T.setBackgroundResource(R.drawable.line_21);
                textViewlineId.setBackgroundResource(R.drawable.line_21);
            } else if (lineId_t[i].equals("22")) {//인천2호선
                T.setBackgroundResource(R.drawable.line_22);
                textViewlineId.setBackgroundResource(R.drawable.line_22);
            } else if (lineId_t[i].equals("102")) {//자기부상철도
                T.setBackgroundResource(R.drawable.line_102);
                textViewlineId.setBackgroundResource(R.drawable.line_102);
            } else if (lineId_t[i].equals("107")) {//용인에버라인
                T.setBackgroundResource(R.drawable.line_107);
                textViewlineId.setBackgroundResource(R.drawable.line_107);
            } else if (lineId_t[i].equals("109")) {//신분당선
                T.setBackgroundResource(R.drawable.line_109);
                textViewlineId.setBackgroundResource(R.drawable.line_109);
            } else if (lineId_t[i].equals("110")) {//의정부경전철
                T.setBackgroundResource(R.drawable.line_110);
                textViewlineId.setBackgroundResource(R.drawable.line_110);
            } else if (lineId_t[i].equals("113")) {//우이신설선
                T.setBackgroundResource(R.drawable.line_113);
                textViewlineId.setBackgroundResource(R.drawable.line_113);
            } else if (lineId_t[i].equals("114")) {//서해선
                T.setBackgroundResource(R.drawable.line_114);
                textViewlineId.setBackgroundResource(R.drawable.line_114);
            } else if (lineId_t[i].equals("115")) {//김포골드라인
                T.setBackgroundResource(R.drawable.line_115);
                textViewlineId.setBackgroundResource(R.drawable.line_115);
            } else if (lineId_t[i].equals("104")) { //경의중앙선
                T.setBackgroundResource(R.drawable.line_104);
                textViewlineId.setBackgroundResource(R.drawable.line_104);
            } else if (lineId_t[i].equals("116")) {//수인분당선
                T.setBackgroundResource(R.drawable.line_116);
                textViewlineId.setBackgroundResource(R.drawable.line_116);
            } else if (lineId_t[i].equals("108")) {//경춘선
                T.setBackgroundResource(R.drawable.line_108);
                textViewlineId.setBackgroundResource(R.drawable.line_108);
            } else if (lineId_t[i].equals("112")) {//경강선
                T.setBackgroundResource(R.drawable.line_112);
                textViewlineId.setBackgroundResource(R.drawable.line_112);
            } else if (lineId_t[i].equals("101")) {//공항철도
                T.setBackgroundResource(R.drawable.line_101);
                textViewlineId.setBackgroundResource(R.drawable.line_101);
            }
        }

    }
}