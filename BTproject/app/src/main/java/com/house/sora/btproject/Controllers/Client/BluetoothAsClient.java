package com.house.sora.btproject.Controllers.Client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.house.sora.btproject.Util.Constants;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by SoRa on 21/5/2016.
 */
public class BluetoothAsClient extends Thread {

    public static final String TAG = "BluetoothAsClient";
    private static  BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;



    public BluetoothAsClient(BluetoothDevice device,Handler mHandler)
    {
        this.mHandler = mHandler;
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket tmp = null;
        mmDevice = device;


        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(Constants.MY_UUID));
            Log.d(TAG,"trying socket");
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try
        {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            Log.d(TAG,"connect....");
            mmSocket.connect();

            // Send message to UI thread that
            // a connection is successfull
            // also the name of the device connected to
            Bundle b = new Bundle();
            b.putInt(Constants.WHAT,Constants.CONNECTION_ESTABLISHED_AS_CLIENT);
            b.putString(Constants.CONNECTED_TO,mmDevice.getName());
            Message msg = new Message();
            msg.setData(b);
            mHandler.sendMessage(msg);
            Log.d(TAG,"Connection established");
        }
        catch (IOException connectException)
        {
            connectException.printStackTrace();
            Log.e(TAG,"Could not connect");
            Bundle b = new Bundle();
            b.putInt(Constants.WHAT,Constants.FAILED_TO_CONNECT);
            Message msg = new Message();
            msg.setData(b);
            mHandler.sendMessage(msg);
            // Unable to connect; close the socket and get out
            try {mmSocket.close();} catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnection(mmSocket);
    }

    private void manageConnection(BluetoothSocket mmSocket)
    {
        IO_Stream_Controller_Client client = new IO_Stream_Controller_Client();
        client.readData();
    }

    public static BluetoothSocket getSocket() {
        return mmSocket;
    }

    /** Will cancel an in-progress connection, and close the socket */
    public static void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}