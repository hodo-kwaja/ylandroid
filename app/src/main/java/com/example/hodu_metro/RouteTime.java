package com.example.hodu_metro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.view.View;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.graphics.Color;
import android.graphics.Typeface;

public class RouteTime extends AppCompatActivity {

    //textview 선언
    TextView duration_textview; //소요시간
    TextView numStep_textview;  //경유역
    TextView transferNum_textview;  //환승 횟수
    TextView stationName_textview;  //역 이름
    TextView lineId_textview;  //호선
    TextView hourminute_textview;  //시,분
    TextView scheduleName_textview;  //방면
    TextView countStation_textview;  //-개 역 이동
    TextView congest_textview;  //혼잡도
    TextView TransferTime_textview;  //환승 시간

    LinearLayout listView; // 레이아웃 객체 생성
    LinearLayout listView2;
    Button createTextView; // 버튼 객체 생성

    String duration_t = "";
    String numStep_t = "";
    String transferNum_t = "";
    String stationName_t = "";

    String scheduleName_t= "";
    String countStation_t= "";
    String congest_t= "";
    int lineId_t = 0;
    int numStep_ = 0;
    String[] hourminute_t = new String[100];
    String[] transferTime_t = new String[100];
    String[] staitonName_tt = new String[100];
    int[] countf= new int[100];
    int[] countl= new int[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_time);

        /////////////////////////////////////////////////////
        //최소환승 버튼 클릭시 화면 전환
        TextView min_transfer=(TextView) findViewById(R.id.min_transfer);
        min_transfer.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteTransfer.class);
                startActivity(intent);
            }
        });

        //혼잡도 버튼 클릭시 화면 전환
        TextView congestion=(TextView) findViewById(R.id.congestion);
        congestion.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteCongest.class);
                startActivity(intent);
            }
        });
        //////////////////////////////////////////////////////////////////

        listView = findViewById(R.id. listView);
        listView2 = findViewById(R.id. listView2);

        //xml 아이디 - textview 지정
        duration_textview = findViewById(R.id.duration);
        numStep_textview = findViewById(R.id.numStep);
        transferNum_textview = findViewById(R.id.transferNum);

        /*hourminute_textview = findViewById(R.id.hourminute);
        stationName_textview = findViewById(R.id.stationName);
        scheduleName_textview = findViewById(R.id.scheduleName);
        countStation_textview = findViewById(R.id.countStation);
        congest_textview = findViewById(R.id.congest);
        TransferTime_textview = findViewById(R.id.TransferTime);
        lineId_textview = findViewById(R.id.lineId);
        */
        AssetManager assetManager = getAssets();

        //assets/ test.json 파일 읽기 위한 InputStream
        try {
            InputStream is = assetManager.open("test1.json");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            StringBuffer buffer = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line + "\n");
                line = reader.readLine();
            }


            String jsonData = buffer.toString();

            //json 데이터가 ShortestPath 일 경우
            JSONObject jsonObject = new JSONObject(jsonData);

            JSONArray jsonArray = jsonObject.getJSONArray("ShortestPath");

            for (int i = 0; i < jsonArray.length(); i++) {

                //전체 역의 값을 갖고오는 object (전체, all)
                JSONObject a = jsonArray.getJSONObject(i);
                //첫번째 역의 값을 갖고오는 object (출발역, first)
                JSONObject f = jsonArray.getJSONObject(0);
                //마지막 역의 값을 갖고오는 object (도착역, last)
                JSONObject l = jsonArray.getJSONObject(jsonArray.length()-1);


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

                hourminute_t[i] = hour +":" + minute;

                //환승이 true 인 역의 숫자 저장
                if(isTransfer == true) {
                    countl[i] = i;
                    if(transferNum == 0 && transferTime != 0||transferNum == 1 && transferTime != 0||transferNum == 2 && transferTime != 0||transferNum == 3 && transferTime != 0) {
                        countf[i] = i;
                        if (transferTime < 60)
                            transferTime_t[i] = transferTime + "초";
                        else {
                            int minute_t = transferTime / 60;
                            int second_t = transferTime % 60;

                            if (second_t == 0) {
                                transferTime_t[i] = minute_t + "분";
                            }
                            transferTime_t[i] = minute_t + "분" + second_t + "초";
                            Log.d("transfer", transferTime_t[i]);
                        }
                    }
                }


                //상단 정보
                int duration_l = schedule_l.getInt("duration");  //소요시간
                int numStep_l = schedule_l.getInt("numStep");  //경유역
                int transferNum_l = transfer_l.getInt("transferNum");  //환승횟수

                if(transferNum_l==1){
                    numStep_ = numStep_l+2;
                }
                else if(transferNum_l==2){
                    numStep_ = numStep_l+3;
                }
                else if(transferNum_l==3){
                    numStep_=numStep_l+4;
                }

                //소요시간(시간,분 으로 변경)
                if(duration_l<60)
                    duration_t = duration_l + "분";
                else {
                    int hour_d = duration_l / 60;
                    int minute_d = duration_l % 60;
                    duration_t =  hour_d + "시간" + minute_d +"분";
                }

                //경유역 개수
                numStep_t = "경유역 " + numStep_l + "개";
                //환승 횟수
                transferNum_t = "환승" + transferNum_l + "회";

            }
            createBigView();

                    /*for(int i=0;i<numStep_;i++) {
                        createSmallView();
                    }*/

            //화면에 출력
            //소요시간, 경유역, 환승횟수
            duration_textview.setText(duration_t);
            numStep_textview.setText(numStep_t);
            transferNum_textview.setText(transferNum_t);

                    /*//출발역 시간,분
                    hourminute_textview.setText(hourminute_t);
                    transferNum_textview.setText(lineId_t);*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createBigView(){

        for(int i=0;i<numStep_;i++) {
            TextView textViewNm = new TextView(getApplicationContext());
            TextView textViewTime= new TextView(getApplicationContext());
            TextView textViewTransfer = new TextView(getApplicationContext());
            textViewNm.setText(staitonName_tt[i]);


            if(i==0){
                textViewTime.setText(hourminute_t[i]);
                textViewTime.setTextSize(13);//열차시간 텍스트 크기 지정
                listView.addView(textViewTime);
                listView.addView(textViewNm);
                textViewNm.setTextSize(23); //출발역 텍스트 크기 지정

            }
            else if(i==countf[i]){
                textViewNm.setTextSize(23);//환승역(첫번째) 텍스트 크기 지정
                textViewTime.setTextSize(13);//열차시간 텍스트 크기 지정
                textViewTime.setText(hourminute_t[i]);
                textViewTransfer.setText(transferTime_t[i]);
                listView.addView(textViewTime);
                listView.addView(textViewNm);
                listView.addView(textViewTransfer);

            }
            else if(i==countl[i]){
                //환승역(두번째) 텍스트 크기 지정
                textViewNm.setTextSize(23);
                textViewTime.setTextSize(13);//열차시간 텍스트 크기 지정
                textViewTime.setText(hourminute_t[i]);
                listView.addView(textViewTime);
                listView.addView(textViewNm);
            }
            else if(i==numStep_-1){
                //도착역 텍스트 크기 지정
                textViewTime.setTextSize(13);//열차시간 텍스트 크기 지정
                textViewTime.setText(hourminute_t[i]);
                textViewNm.setTextSize(23);
                listView.addView(textViewTime);
                listView.addView(textViewNm);
            }
            else{
                //경유역 텍스트 크기 지정
                textViewNm.setTextSize(15);
                listView.addView(textViewNm);
            }

            //4. 텍스트뷰 글자타입 설정
            textViewNm.setTypeface(null, Typeface.BOLD);
            textViewNm.setTextColor(Color.rgb(0, 0, 0));

            //5. 텍스트뷰 ID설정
            textViewNm.setId(i);
            textViewTransfer.setId(i);
            textViewTime.setId(i);

            //6. 레이아웃 설정
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param.leftMargin = 230; //출발,도착역
            param.bottomMargin = 15;
            param.topMargin = 12;

            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param1.leftMargin =70; //열차시간
            param1.topMargin = 5;
            param1.bottomMargin=-80;

            LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            param3.leftMargin = 230; //환승역
            param3.bottomMargin = 80;
            param3.topMargin = 40;

            // 7. 설정한 레이아웃 텍스트뷰에 적용
            textViewNm.setLayoutParams(param);
            textViewTime.setLayoutParams(param1);
            textViewTransfer.setLayoutParams(param3);

            //8. 텍스트뷰 백그라운드색상 설정
            textViewNm.setBackgroundColor(Color.rgb(255, 255, 255));
            textViewTransfer.setBackgroundColor(Color.rgb(255, 255, 255));
            textViewTime.setBackgroundColor(Color.rgb(255, 255, 255));

            //9. 생성및 설정된 텍스트뷰 레이아웃에 적용

        }
    }

    //뒤로가기 버튼 누르면 Input 액티비티로 이동
    @Override public void onBackPressed() {

        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(),Input.class));

    }


    /*private void createSmallView(){
        //1. 텍스트뷰 객체생성
        TextView textViewNm = new TextView(getApplicationContext());
        //2. 텍스트뷰에 들어갈 문자설정
        textViewNm.setText(stationName_t);
        //3. 텍스트뷰 글자크기 설정
        textViewNm.setTextSize(15);//텍스트 크기
        //4. 텍스트뷰 글자타입 설정
        textViewNm.setTypeface(null, Typeface.NORMAL);
        //5. 텍스트뷰 ID설정
        textViewNm.setId(0);
        //6. 레이아웃 설정
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                ,LinearLayout.LayoutParams.WRAP_CONTENT);
        param.leftMargin = 30;
        param.bottomMargin=12;
        param.topMargin=12;
        // 7. 설정한 레이아웃 텍스트뷰에 적용
        textViewNm.setLayoutParams(param);
        //8. 텍스트뷰 백그라운드색상 설정
        textViewNm.setBackgroundColor(Color.rgb(255,255,255));
        //9. 생성및 설정된 텍스트뷰 레이아웃에 적용
        listView.addView(textViewNm);
    }*/

}

