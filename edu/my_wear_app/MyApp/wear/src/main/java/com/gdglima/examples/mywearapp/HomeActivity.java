package com.gdglima.examples.mywearapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends Activity {

    public static String TAG="HomeActivity";
    private TextView mTextView;

    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                View iviLogo = stub.findViewById(R.id.iviLogo);
                iviLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v(TAG, "iviLogo");

                       // checkConnected();// BTAdapter.startDiscovery();
                        registerReceiver(my_receiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                    }
                });
            }
        });


    }

    private final BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.v(TAG, "action "+action);
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                //int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                Log.v(TAG, "name "+name + " rssi "+rssi+ " dBm\\n");
                //Toast.makeText(getApplicationContext(),"  RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //-----------------------------------------------------
    public void checkConnected()
    {
        // true == headset connected && connected headset is support hands free
        int state = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET);
        if (state != BluetoothProfile.STATE_CONNECTED)
            return;

        try
        {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(HomeActivity.this, serviceListener, BluetoothProfile.HEADSET);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener()
    {
        @Override
        public void onServiceDisconnected(int profile)
        {

        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy)
        {
            for (BluetoothDevice device : proxy.getConnectedDevices())
            {
                Log.i("onServiceConnected", "|" + device.getName() + " | " + device.getAddress() + " | " + proxy.getConnectionState(device) + "(connected = "
                        + BluetoothProfile.STATE_CONNECTED + ")");
            }

            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
        }
    };

    private final BroadcastReceiver my_receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String mIntentAction = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(mIntentAction)) {
                int RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String mDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

                Log.v(TAG, "RSSI "+RSSI+" devicename "+mDeviceName);
            }
        }
    };
}
