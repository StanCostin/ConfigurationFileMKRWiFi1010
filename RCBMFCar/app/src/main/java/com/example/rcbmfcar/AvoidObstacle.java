package com.example.rcbmfcar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class AvoidObstacle extends AppCompatActivity {

    private Button btnObsOn, btnObsOff, btnObsDisc;
    private String addressAO;
    private ProgressDialog progress;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avoid_obstacle);

        Intent aoi = getIntent();
        addressAO = aoi.getStringExtra(MenuController.EXTRA_AM);

        btnObsOn = findViewById(R.id.buttonOnObstacle);
        btnObsOff = findViewById(R.id.buttonOffObstacle);
        btnObsDisc = findViewById(R.id.buttonDisconnectObstacle);

        new AvoidObstacle.ConnectBT().execute();

        btnObsOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalOn("Obstacle");
            }
        });

        btnObsOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalOff("Obstacle");
            }
        });

        btnObsDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalDisconnect("Obstacledisc");
                disconnect();
            }
        });

    }

    private void message (String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(AvoidObstacle.this, "Bluetooth comunication in progress...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dev = myBluetooth.getRemoteDevice(addressAO);
                    btSocket = dev.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                message("Connection Failed. Try again.");
                finish();
            } else {
                message("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }

    private void signalOn ( String aoOn ) {
        if ( btSocket != null ) {
            try {
                String aoValue = aoOn.toUpperCase() + " ON:";
                btSocket.getOutputStream().write(aoValue.getBytes());
            } catch (IOException e) {
                message("Error");
                disconnect();
            }
        }
    }

    private void signalOff ( String aoOff ) {
        if ( btSocket != null ) {
            try {
                String aoValue = aoOff.toUpperCase() + " OFF:";
                btSocket.getOutputStream().write(aoValue.getBytes());
            } catch (IOException e) {
                message("Error");
                disconnect();
            }
        }
    }

    private void signalDisconnect ( String dsc ) {
        if ( btSocket != null ) {
            try {
                String dscValue = dsc.toUpperCase() + ":";
                btSocket.getOutputStream().write(dscValue.getBytes());
            } catch (IOException e) {
                message("Error");
            }
        }
    }

    private void disconnect () {
        if ( btSocket!=null ) {
            try {
                message("Connection closed");
                btSocket.close();
            } catch(IOException e) {
                message("Error");
            }
        }

        finish();
    }


}