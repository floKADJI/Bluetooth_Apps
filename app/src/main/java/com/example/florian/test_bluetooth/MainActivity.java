package com.example.florian.test_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button button, button1, button2;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        button = (Button) findViewById(R.id.enableBT);
        button1 = (Button) findViewById(R.id.listDevice);
        button2 = (Button) findViewById(R.id.searchDevice);
        listView = (ListView) findViewById(R.id.displayList);

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
            Toast.makeText(MainActivity.this, "Bluetooh not available", Toast.LENGTH_LONG).show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mBluetoothAdapter.isEnabled()){
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, 0);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth already enable", Toast.LENGTH_LONG).show();
                }
            }
        });

        ArrayList<String> devices = new ArrayList<>();
        Set<BluetoothDevice> pairedBluetooth = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedBluetooth){
            devices.add(device.getName());
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices );

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(adapter);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothAdapter.startDiscovery();
//                if (mBluetoothAdapter.startDiscovery())
//                    Toast.makeText(getApplicationContext(),"Scan_finish", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == 0){
            // Make sure the request was successful
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Bluetooth enable", Toast.LENGTH_LONG).show();
            }
        }
    }

//    Create a BroadcastReceiver for ACTION_STATE_CHANGED
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ( BluetoothDevice.ACTION_FOUND.equals(action) ) {
//            Discovery has found a device. Get the BluetoothDevice object and its info from the Intent.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address

            Toast.makeText(getApplicationContext(), "BT:" + deviceName, Toast.LENGTH_LONG).show();;
        }
    }
};

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }
}
