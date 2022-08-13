package com.example.hodu_metro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.graphics.Color;
import android.graphics.Typeface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.widget.Button;


public class RouteTime extends AppCompatActivity {

    //textview 선언
    TextView duration_textview;
    TextView numStep_textview;
    TextView transferNum_textview;

    LinearLayout listView; // 레이아웃 객체 생성
    Button createTextView; // 버튼 객체 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_time);

        //////////////////////////////////////////////////////////////////////////////
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

        /////////////////////////////////////////////////////////////////////////////
        
        //xml 아이디 - textview 지정
        duration_textview = findViewById(R.id.duration);
        numStep_textview = findViewById(R.id.numStep);
        transferNum_textview = findViewById(R.id.transferNum);

        AssetManager assetManager = getAssets();

        //assets/ test.json 파일 읽기 위한 InputStream
        try {
            InputStream is = assetManager.open("test.json");
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

            String duration_t = "";
            String numStep_t = "";
            String transferNum_t = "";

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo = jsonArray.getJSONObject(i);

                //마지막 역의 값을 갖고오는 object
                JSONObject jf = jsonArray.getJSONObject(0);

                //string 예시
                //String stationName = jo.getString("stationName");

                JSONObject transfer = jo.getJSONObject("transfer");

                JSONObject schedule = jf.getJSONObject("schedule");
                int duration = schedule.getInt("duration");
                int numStep = schedule.getInt("numStep");
                int transferNum = schedule.getInt("transferNum");

                //소요시간
                if(duration<60)
                    duration_t = duration + "분";
                else {
                    int hour = duration / 60;
                    int minute = duration % 60;
                    duration_t =  hour + "시간" + minute +"분";
                }

                //경유역 개수
                numStep_t = "경유역 " + numStep + "개";
                //환승 횟수
                transferNum_t = "환승" + transferNum + "회";

            }
            //화면에 출력
            duration_textview.setText(duration_t);
            numStep_textview.setText(numStep_t);
            transferNum_textview.setText(transferNum_t);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView = findViewById(R.id. listView);
        createTextView=findViewById(R.id.createTextView);

        createTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createBigView();

                int cnt=5;
                for(int i=0;i<cnt;i++) {
                    createSmallView();
                }
            }
        });

    }
    private void createBigView(){

        //1. 텍스트뷰 객체생성
        TextView textViewNm = new TextView(getApplicationContext());

        //2. 텍스트뷰에 들어갈 문자설정
        textViewNm.setText("출발역");

        //3. 텍스트뷰 글자크기 설정
        textViewNm.setTextSize(20);//텍스트 크기

        //4. 텍스트뷰 글자타입 설정
        textViewNm.setTypeface(null, Typeface.BOLD);

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
    }

    private void createSmallView(){

        //1. 텍스트뷰 객체생성
        TextView textViewNm = new TextView(getApplicationContext());

        //2. 텍스트뷰에 들어갈 문자설정
        textViewNm.setText("경유역");

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
    }




}
