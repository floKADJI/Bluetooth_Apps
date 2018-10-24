package com.example.florian.test_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.MacAddress;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button button, button1, button2;
    ListView listView;
    ArrayList<String> deviceFounded;

    static final String DEFAULT_MAC_ADDRESS = "68:a3:c4:65:6c:a8";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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

        final ArrayList<String> devices = new ArrayList<>();
        final Set<BluetoothDevice> pairedBluetooth = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedBluetooth){
            devices.add(device.getAddress());
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
//                ConnectThread connectThread = new ConnectThread();
//                connectThread.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String adressBT = ((TextView)view).getText().toString();
                BluetoothDevice address = mBluetoothAdapter.getRemoteDevice(adressBT);

                ConnectThread connect = new ConnectThread(address);
                connect.start();
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

//    Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ( BluetoothDevice.ACTION_FOUND.equals(action) ) {
//            Discovery has found a device. Get the BluetoothDevice object and its info from the Intent.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address

            deviceFounded = new ArrayList<>();
            Toast.makeText(getApplicationContext(), "BT:" + deviceName, Toast.LENGTH_LONG).show();
            deviceFounded.add(deviceName);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, deviceFounded );
            listView.setAdapter(adapter);
        }
    }
};

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }


    public class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice mDevice) {
//            Use a temporary object that is later assigned to mmSocket because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = mDevice;

            try {
//                Get a BluetoothSocket to connect with the given BluetoothDevice.
                tmp = mmDevice.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                Log.e("ConnectThread", "Socket's create() method", e);
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
//            Cancel discovery because it otherwise slows down the connection.

            try {
//                Connect to the remote device through the socket. This call blocks until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
//                Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("ConnectThread", "Could not close the client socket", closeException);
                }
                return;
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("ConnetThread", "Could not close the client socket", e);
            }
        }

    }
}

