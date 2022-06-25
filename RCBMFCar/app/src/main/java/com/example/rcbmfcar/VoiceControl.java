package com.example.rcbmfcar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class VoiceControl extends AppCompatActivity {

    private ImageButton micButton;
    private Button btnVcDisc;
    private EditText textSpeech;
    private final static int RECORD_AUDIO_REQUEST_CODE = 10;
    private String addressVC;
    private ProgressDialog progress;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_control);

        Intent vci = getIntent();
        addressVC = vci.getStringExtra(MenuController.EXTRA_AM);

        micButton = findViewById(R.id.buttonMic);
        textSpeech = findViewById(R.id.editTextSpeech);
        btnVcDisc = findViewById(R.id.buttonVcDisconnect);

        new VoiceControl.ConnectBT().execute();

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeachInput();
            }
        });

        btnVcDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalDisconnect("Disable");
                disconnect();
            }
        });

    }

    public void getSpeachInput() {

        Intent voiceControlIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceControlIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceControlIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        voiceControlIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something....");

        startActivityForResult(voiceControlIntent, RECORD_AUDIO_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RECORD_AUDIO_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> dataVoice = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textSpeech.setText(dataVoice.get(0));
                    sendDataVoice(dataVoice.get(0));
                }
                break;
        }
    }

    private void message (String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(VoiceControl.this, "Bluetooth comunication in progress...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dev = myBluetooth.getRemoteDevice(addressVC);
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

    private void sendDataVoice ( String aoOn ) {
        if ( btSocket != null ) {
            try {
                String aoValue = "!" + aoOn.toUpperCase() + ":";
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