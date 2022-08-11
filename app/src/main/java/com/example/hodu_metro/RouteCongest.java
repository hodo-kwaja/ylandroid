package com.example.hodu_metro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hodu_metro.R;

public class RouteCongest extends AppCompatActivity {
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




    }
}