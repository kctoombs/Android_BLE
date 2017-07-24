package com.example.ktoombs.salesrepapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothConnect extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> deviceSet;
    private ArrayList<String> pairedDevices;   //Names of the devices that have been paired
    ArrayList<BluetoothDevice> devices;        //Discovered devices
    private ArrayAdapter<String> listAdapter;
    private IntentFilter filter;
    private BroadcastReceiver receiver;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_discover);
        pairedDevices = new ArrayList<>();
        devices = new ArrayList<BluetoothDevice>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(listAdapter);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String theAction = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(theAction)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    String s = "";
                    for(int i = 0; i < pairedDevices.size(); i++){
                        if(device.getName().equals(pairedDevices.get(i))){
                            s = "(Paired)";
                            break;
                        }
                    }

                    listAdapter.add(device.getName() + " " + s + " " + "\n"+device.getAddress());
                }

                else if(mBluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(theAction)){

                }

                else if(mBluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(theAction)){

                }

                else if(mBluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(theAction)){
                    if(mBluetoothAdapter.getState() == mBluetoothAdapter.STATE_OFF){
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
            }
        };
        checkIfBluetoothEnabled();
        getPairedDevices();
        //Look for devices with bluetooth capability that are discoverable
        startDiscovery();
    }

    private void startDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    }

    private void checkIfBluetoothEnabled() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Toast.makeText(this.getApplicationContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }

        //Has bluetooth capability, check if it is enabled now
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Toast.makeText(this.getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this.getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_LONG).show();
        }
    }

    private void getPairedDevices(){
        //Returns the set of BluetoothDevice objects that are bonded (paired) to the local adapter
        deviceSet = mBluetoothAdapter.getBondedDevices();
        if(deviceSet.size() > 0){
            for(BluetoothDevice d : deviceSet){
                pairedDevices.add(d.getName());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}


