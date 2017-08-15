package com.example.android.bluehealth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BT_Connect extends AppCompatActivity {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    ArrayAdapter<String> listAdapter;
    Button turnOnBT;
    Button turnOffBT;
    Button disconnect;
    Button dataView;
    ListView listView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BT_Connect";





    //onCreate: initialise
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt__connect);
        init();


    }

    //put paired devices into array list
    private void getPairedDevices() {
        devicesArray = btAdapter.getBondedDevices();
        if (devicesArray.size() > 0) {
            for (BluetoothDevice device : devicesArray) {
                listAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }


    // init() initialises button view adapter etc
    private void init() {
        turnOnBT = (Button) findViewById(R.id.turnOnButton);
        turnOffBT = (Button) findViewById(R.id.turnOffButton);
        disconnect = (Button) findViewById(R.id.disconnectButton);
        dataView = (Button) findViewById(R.id.dataViewButton);
        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        listView.setAdapter(listAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        listView.setOnItemClickListener(mDeviceClickListener);
        getPairedDevices();
        //check if mobile device supports bluetooth
        if (btAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getBaseContext(), "Your device does not support bluetooth", Toast.LENGTH_SHORT).show();
        }


        //What happens when turnOnBt button is clicked
        turnOnBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btAdapter.enable();
                Toast.makeText(getBaseContext(), "Enabling Bluetooth", Toast.LENGTH_SHORT).show();

            }
        });


        //What happens when turnOffBt button is clicked
        turnOffBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btAdapter.disable();
                Toast.makeText(getBaseContext(), "Disabling Bluetooth", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void dataTransferView(View v) {
        //change view to bt menu
        setContentView(R.layout.activity_data_transfer);
    }
    public void bluetoothMenuView(View v) {
        //change view to bt menu
        setContentView(R.layout.activity_bt__connect);
        init();
    }



    //on-click listener: what happens when device on list is clicked
    public AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery
            btAdapter.cancelDiscovery();
            Log.d(TAG, "onclickitem you clicked a device");

            // Get the device MAC address, which is the last 17 characters in the View
            String info = ((TextView) v).getText().toString();
            String deviceAddress = info.substring(info.length() - 17);

            Log.d(TAG, "onItemClick: info: " + info);
            Log.d(TAG, "onItemClick: deviceAddress: " + deviceAddress);

            BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

            new ConnectThread(device);
            Log.d(TAG, "onItemClick: ConnectThread method called");

            String address = "98:D3:31:FD:20:A9";
            if (!deviceAddress.equals(address)) {
                Toast.makeText(getBaseContext(), "This device is not compatible with this application", Toast.LENGTH_SHORT).show();
            } else {
                // Make an intent to start next activity while taking an extra which is the MAC address.
                Intent i = new Intent(BT_Connect.this, DataTransfer.class);
                i.putExtra(EXTRA_DEVICE_ADDRESS, address);
                startActivity(i);
                Toast.makeText(getBaseContext(), "Starting new activity data transfer", Toast.LENGTH_SHORT).show();
            }
        }
    };


    public class ConnectThread extends Thread {
        public BluetoothSocket mmSocket;
        public BluetoothDevice mmDevice;


        public ConnectThread(BluetoothDevice device) {
            disconnect = (Button) findViewById(R.id.disconnectButton);
            Log.d(TAG, "ConnectThread: creating connection");
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {

                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                Log.d(TAG, "ConnectThread:  connection created" +mmDevice +tmp);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread:  connection failed");
            }
            mmSocket = tmp;
            run();
        }





        public void run() {
            btAdapter.cancelDiscovery();
            Log.d(TAG, "run.connect: discovery cancelled and establishing connection");
            try {
                mmSocket.connect();
                Toast.makeText(getBaseContext(), "Connected to: \n" + mmDevice, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "run.connect: connection ok");
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: connection closed");
                } catch (IOException closeException) {
                    Log.d(TAG, "run: exception error connection closed");
                }
                return;
            }
        }



        public void cancel() {
            try {
                mmSocket.getInputStream().reset();
                mmSocket.close();
                Log.d(TAG, "cancel: connection closed");

            } catch (IOException e) {
            }
        }
    }



}






