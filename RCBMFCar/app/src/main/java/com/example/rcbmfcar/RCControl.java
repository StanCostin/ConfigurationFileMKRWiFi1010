package com.example.rcbmfcar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class RCControl extends AppCompatActivity {

    private ImageButton btnUp;
    private ImageButton btnDwn;
    private ImageButton btnL;
    private ImageButton btnR;
    private ImageButton btnHornCar;
    private ImageButton btnFrontLights;
    private ImageButton btnBackLights;
    private ImageButton btnDisconnect;
    private String addressRC;
    private ProgressDialog progress;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private boolean isClicked, isClicked1 = false;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rccontrol);

        Intent rci = getIntent();
        addressRC = rci.getStringExtra(MenuController.EXTRA_AM);

        btnUp = findViewById(R.id.idarrowUp);
        btnDwn = findViewById(R.id.idarrowDown);
        btnL = findViewById(R.id.idarrowleft);
        btnR = findViewById(R.id.idarrowright);
        btnFrontLights = findViewById(R.id.idFrontLights);
        btnBackLights = findViewById(R.id.idBacklights);
        btnHornCar = findViewById(R.id.idHornCar);
        btnDisconnect = findViewById(R.id.idDisconnect);

        new RCControl.ConnectBT().execute();

        btnUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {

                    signalOn("Up");
                    return false;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    signalOff("Stop");
                    return false;
                }
                return false;
            }
        });

        btnDwn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {

                    signalOn("Down");
                    return false;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    signalOff("Stop");
                    return false;
                }
                return false;
            }
        });

        btnL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {

                    signalOn("Left");
                    return false;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    signalOff("Stop");
                    return false;
                }
                return false;
            }
        });

        btnR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {

                    signalOn("Right");
                    return false;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    signalOff("Stop");
                    return false;
                }
                return false;
            }
        });

        btnHornCar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {

                    signalOn("Honk");
                    return false;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    signalOff("Stop Honk");
                    return false;
                }
                return false;
            }
        });

        btnFrontLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClicked == false)
                {
                    signalOn("Turn On Front Lights");
                    isClicked = true;
                }
                else  {
                    signalOff("Turn Off Front Lights");
                    isClicked = false;
                }



            }
        });

        btnBackLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClicked1 == false)
                {
                    signalOn("Turn On Back Lights");
                    isClicked1 = true;
                }
                else {
                    signalOff("Turn Off Back Lights");
                    isClicked1 = false;
                }

            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalDisconnect("Disconnect");
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
            progress = ProgressDialog.show(RCControl.this, "Bluetooth comunication in progress...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dev = myBluetooth.getRemoteDevice(addressRC);
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

    private void signalOn ( String rcOn ) {
        if ( btSocket != null ) {
            try {
                String rcValue = rcOn.toUpperCase() + ":";
                btSocket.getOutputStream().write(rcValue.getBytes());
            } catch (IOException e) {
                message("Error");
                disconnect();
            }
        }
    }

    private void signalOff ( String rcOff ) {
        if ( btSocket != null ) {
            try {
                String rcValue = rcOff.toUpperCase() + ":";
                btSocket.getOutputStream().write(rcValue.getBytes());
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