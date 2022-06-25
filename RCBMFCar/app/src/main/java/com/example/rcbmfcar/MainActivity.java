package com.example.rcbmfcar;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button bton, btoff, btpd;
    private ListView listDevices;
    private BluetoothAdapter bltAdapt;
    private Set<BluetoothDevice> devices;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    public static String EXTRA_ADDR = "adress";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bltAdapt = BluetoothAdapter.getDefaultAdapter();
        bton = findViewById(R.id.buttonOn);
        btoff = findViewById(R.id.buttonOff);
        btpd = findViewById(R.id.buttonDevices);
        listDevices = findViewById(R.id.ListViewid);

        if(bltAdapt == null) {

            Toast.makeText(getApplicationContext(), "Bluetooth is not available!", Toast.LENGTH_LONG).show();
            finish();
        }

        bton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bltAdapt.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "Turning On Bluetooth...", Toast.LENGTH_LONG).show();
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, REQUEST_ENABLE_BT);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_LONG).show();
                }
            }
        });

        btoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bltAdapt.isEnabled()) {
                    bltAdapt.disable();
                    Toast.makeText(getApplicationContext(), "Turning Off Bluetooth...", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is already off", Toast.LENGTH_LONG).show();
                }
            }
        });

        btpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairDevsList();
            }
        });

    }

    private void pairDevsList() {
        devices = bltAdapt.getBondedDevices();
        ArrayList list = new ArrayList();

        if(devices.size() > 0)
        {
            for(BluetoothDevice bdev : devices)
            {
                list.add(bdev.getName().toString() + "\n" + bdev.getAddress().toString());
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "No paired devices discovered", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listDevices.setAdapter(adapter);
        listDevices.setOnItemClickListener(myListClickListener);

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length()-17);

            Intent i = new Intent(MainActivity.this, MenuController.class);
            i.putExtra(EXTRA_ADDR, address);
            startActivity(i);
        }

    };

}