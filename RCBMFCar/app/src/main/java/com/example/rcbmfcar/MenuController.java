package com.example.rcbmfcar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuController extends AppCompatActivity {

    private Button btnrc;
    private Button btnoa;
    private Button btnvc;
    private Button btndc;
    private String addr = null;
    public static String EXTRA_AM = "address bluetooth";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_controller);

        Intent intent = getIntent();
        addr = intent.getStringExtra(MainActivity.EXTRA_ADDR);

        btnrc = findViewById(R.id.buttonRcController);
        btnoa = findViewById(R.id.btnObstacleAvoidance);
        btnvc = findViewById(R.id.btnVocalControl);
        btndc = findViewById(R.id.btnDanceControl);

        btnrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rcAddress = addr;
                Intent rca = new Intent(MenuController.this, RCControl.class);
                rca.putExtra(EXTRA_AM, rcAddress);
                startActivity(rca);
            }
        });

        btnoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oaAddress = addr;
                Intent oaa = new Intent(MenuController.this, AvoidObstacle.class);
                oaa.putExtra(EXTRA_AM, oaAddress);
                startActivity(oaa);
            }
        });

        btnvc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vcAddress = addr;
                Intent vca = new Intent(MenuController.this, VoiceControl.class);
                vca.putExtra(EXTRA_AM, vcAddress);
                startActivity(vca);
            }
        });

        btndc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rcAddress = addr;
                Intent rca = new Intent(MenuController.this, DanceControl.class);
                rca.putExtra(EXTRA_AM, rcAddress);
                startActivity(rca);
            }
        });


    }

}