package com.example.ktoombs.salesrepapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ktoombs on 7/17/17.
 */

public class BLEConnect extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;  //10 seconds for scanning
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> listAdapter;
    private boolean isScanning;
    private Handler mHandler;
    private Button scanButton;
    private ListView deviceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, 0);
        deviceList = (ListView) findViewById(R.id.listView);
        deviceList.setAdapter(listAdapter);

        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapter.clear();
                scanLeDevice(true);
            }
        });

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        checkIfBluetoothEnabled();
    }

    private void checkIfBluetoothEnabled() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this.getApplicationContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
            //Can't do anything if device doesn't support BT
            finish();
        }

        //Check for BLE feature
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this.getApplicationContext(), "Device does not support BLE", Toast.LENGTH_LONG).show();
            finish();
        }

        //Has bluetooth capability, check if it is enabled now
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(this.getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_LONG).show();
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            isScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            isScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Initializes list view adapter.
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, 0);
        deviceList.setAdapter(listAdapter);
        scanLeDevice(true);
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.add(device.getName());
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

}
