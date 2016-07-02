package com.house.sora.btproject.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.house.sora.btproject.R;
import com.house.sora.btproject.Util.BluetoothDeviceArrayAdapter;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by SoRa on 3/6/2016.
 */
public class FindDevice extends AppCompatActivity
{
    private static final String TAG = "FindDeviceActivity";
    private static ListView pairedDevicesListView, devicesListView;
    private static ArrayList<BluetoothDevice> pairedDevicesArrayList,devicesArrayList;
    private static ArrayAdapter<BluetoothDevice> pairedDevicesArrayAdapter,devicesArrayAdapter;
    private BluetoothAdapter btAdapter;
    private BroadcastReceiver mReceiver,mReceiver1,mReceiver2;
    private Button btnSearchDevices;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list_layout);

        init();
        registerReceivers();
        fillPairedDevicesListView();
    }

    private void fillPairedDevicesListView() {
        Set<BluetoothDevice> list = btAdapter.getBondedDevices();
        // Add the paired devices to the list
        // and notify the adapter to repaint
        pairedDevicesArrayList.addAll(list);
        pairedDevicesArrayAdapter.notifyDataSetChanged();
    }


    private void init()
    {
        // ActionBar title
        setTitle("Find Device");

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        pairedDevicesListView = (ListView) findViewById(R.id.listViewPairedDevices);
        devicesListView = (ListView) findViewById(R.id.ListViewDevices);

        pairedDevicesArrayList = new ArrayList<>();
        devicesArrayList = new ArrayList<>();

        pairedDevicesArrayAdapter = new BluetoothDeviceArrayAdapter(this,android.R.layout.simple_list_item_single_choice,pairedDevicesArrayList);
        devicesArrayAdapter = new BluetoothDeviceArrayAdapter(this,android.R.layout.simple_list_item_single_choice,devicesArrayList);

        pairedDevicesListView.setAdapter(pairedDevicesArrayAdapter);
        devicesListView.setAdapter(devicesArrayAdapter);

        pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   pairedDevicesListViewClicked(parent,view,position,id); }
        });

        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   devicesArrayAdapterClicked(parent, view, position, id);      }
        });

        btnSearchDevices = (Button) findViewById(R.id.btnSearchDevices);
        btnSearchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  btnSearchDevicesClicked(v);}    });

    }

    private void btnSearchDevicesClicked(View v)
    {
        // Check if bluetooth is enabled
        if (btAdapter.isEnabled()) {
            // Start discovering servicce to find new device
            btAdapter.startDiscovery();
        }
        else
        {
            // Display error message
            Toast.makeText(getApplicationContext(),"Enable bluetooth to start searching",Toast.LENGTH_SHORT).show();
        }
    }


    private void registerReceivers()
    {
        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver= new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    // if it contains the device skip it
                    if(devicesArrayList.contains(device)); // Do nothing
                    else devicesArrayList.add(device);

                    devicesArrayAdapter.notifyDataSetChanged();
                    Log.d(TAG,"Added Device to the List\n");
                    Log.d(TAG,device.getName()+"\n"+device.getAddress());
                }
            }
        };

        // Register the BroadcastReceiver for informing about device found
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        // Create a BroadcastReceiver for ACTION_DISCOVERY_FINISHED
        mReceiver1= new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if(action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                {
                    // Make a toast informing the user that search is stopped
                    // remove progressbar
                    Toast.makeText(getApplicationContext(),"Search stopped",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
        };

        // Register the BroadcastReceiver for informing about discovery finished
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver1, filter1); // Don't forget to unregister during onDestroy

        // Create a BroadcastReceiver for ACTION_DISCOVERY_STARTED
        mReceiver2= new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action == BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                {
                    Toast.makeText(getApplicationContext(),"Search Started",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver2, filter2); // Don't forget to unregister during onDestroy

    }





    private void devicesArrayAdapterClicked(AdapterView<?> parent, View view, int position, long id)
    {
        Log.d(TAG,"deviceClicked");

        //Get the device name clicked .
        BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);

        // Create new Intent and put the bluetooth device inside
        // then setResult to OK and finish this activity
        Intent intentToReturn = new Intent();
        intentToReturn.putExtra("Device",device);
        setResult(RESULT_OK,intentToReturn);
        finish();

    }

    private void pairedDevicesListViewClicked(AdapterView<?> parent, View view, int position, long id)
    {
        Log.d(TAG,"Paired_deviceClicked");

        //Get the device name clicked .
        BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);

        // Create new Intent and put the bluetooth device inside
        // then setResult to OK and finish this activity
        Intent intentToReturn = new Intent();
        intentToReturn.putExtra("Device",device);
        setResult(RESULT_OK,intentToReturn);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the receivers
        // and cancelDiscovery
        unregisterReceiver(mReceiver);
        unregisterReceiver(mReceiver1);
        unregisterReceiver(mReceiver2);
        btAdapter.cancelDiscovery();
    }

}
