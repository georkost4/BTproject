package com.house.sora.btproject.Client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.house.sora.btproject.ASYNC_TASKS.readIncomingData_ASYNC;
import com.house.sora.btproject.MainActivity;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by SoRa on 21/5/2016.
 */
public class BluetoothAsClient extends Thread {
    private static final String MY_UUID = "f49beda8-1f8b-11e6-b6ba-3e1d05defe78";
    private static final String TAG = "BluetoothAsClient";
    private static  BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;


    public BluetoothAsClient(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket tmp = null;
        mmDevice = device;


        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            Log.d(TAG,"Connection established");
        }
        catch (IOException connectException)
        {
            // Unable to connect; close the socket and get out
            try {mmSocket.close();} catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnection(mmSocket);
    }

    private void manageConnection(BluetoothSocket mmSocket)
    {
        BluetoothDevice device =  mmSocket.getRemoteDevice();
        Log.d(TAG,"Connected to : "+device.getName());

        MainActivity.startIncoming(mmSocket);

    }

    public static BluetoothSocket getSocket() {
        return mmSocket;
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}