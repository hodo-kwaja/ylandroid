package com.example.hodu_metro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RouteTransfer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_transfer);

        //최단시간 버튼 클릭시 화면 전환
        TextView shortest_time=(TextView) findViewById(R.id.shortest_time);
        shortest_time.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteTime.class);
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
    }

    @Override public void onBackPressed() {

        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(),Input.class));

    }
}