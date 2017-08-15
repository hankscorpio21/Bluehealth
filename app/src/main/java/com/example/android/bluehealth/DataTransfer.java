package com.example.android.bluehealth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Handler;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;


public class DataTransfer extends AppCompatActivity {


    private static final Object MESSAGE_READ = 1 ;
    private ConnectedThread mConnectedThread;
    private int mState;
    private int mNewState;
    Button bluetoothMenu;
    TextView xValueView;
    TextView yValueView;
    TextView zValueView;
    TextView tempView;
    private String TAG = "Data Transfer Activity";
    private ConnectedThread ConnectedThread;
    public Socket mmSocket;
    public BluetoothAdapter btAdapter;
    public String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);
        initUI();
    }

    public void initUI() {
        bluetoothMenu = (Button) findViewById(R.id.bluetoothMenu);
        xValueView = (TextView) findViewById(R.id.xValueView);
        yValueView = (TextView) findViewById(R.id.yValueView);
        zValueView = (TextView) findViewById(R.id.zValueView);
        tempView = (TextView) findViewById(R.id.tempView);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_bt__connect);
                Toast.makeText(getBaseContext(), "Bluetooth Device Menu", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void bluetoothMenuView(View v) {
        //change view to bt menu
        setContentView(R.layout.activity_bt__connect);
    }


    public void dataTransferView(View view) {
        setContentView(R.layout.activity_data_transfer);
    }



    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the BT_Connect activity via EXTRA
        address = intent.getStringExtra(com.example.android.bluehealth.BT_Connect.EXTRA_DEVICE_ADDRESS);
        Log.d(TAG, "onResume: get address"+ com.example.android.bluehealth.BT_Connect.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);


        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //mConnectedThread.write("x");
    }




    private class ConnectedThread extends Thread {

        private static final String TAG = "CONNECTED THREAD";
        private final Socket socket = null;
        private final InputStream mmInStream;
        public Handler mHandler;



        public ConnectedThread(Socket mmSocket) {
                Log.d(TAG, "create ConnectedThread: " );
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the BluetoothSocket input and output streams
                try {
                    tmpIn = socket.getInputStream();
                } catch (IOException e) {
                    Log.e(TAG, "temp sockets not created", e);
                }

                mmInStream = tmpIn;
                mState = STATE_CONNECTED;
            }



        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity


                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    break;
                }
            }
        }



        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private void sendToTarget() {

    }

}


        //call new data transfer activity




