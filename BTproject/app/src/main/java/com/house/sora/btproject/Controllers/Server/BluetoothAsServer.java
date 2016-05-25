package com.house.sora.btproject.Controllers.Server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.house.sora.btproject.Util.Constants;

import java.io.IOException;
import java.util.UUID;

public class BluetoothAsServer extends Thread 
{
    private static final String TAG = "BluetoothAsServer";
    private final BluetoothServerSocket mmServerSocket;
    private  BluetoothAdapter mBluetoothAdapter;
    private static BluetoothSocket socket = null;
    private Handler mHandler;

    public BluetoothAsServer(Handler mHandler)
    {
        this.mHandler = mHandler;
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;
        try
        {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(Constants.NAME, UUID.fromString(Constants.MY_UUID));
        }
        catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run()
    {

        // Keep listening until exception occurs or a socket is returned
        while (true)
        {
            try
            {
                socket = mmServerSocket.accept();
                Bundle b = new Bundle();
                b.putInt(Constants.WHAT,Constants.CONNECTION_ESTABLISHED_AS_SERVER);
                Message msg = new Message();
                msg.setData(b);
                mHandler.sendMessage(msg);
                Log.d(TAG,"Connection as Server Established");
            }
            catch (IOException e) {break;  }
            // If a connection was accepted
            if (socket != null)
            {
                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(socket);
                try
                {
                    mmServerSocket.close();
                }
                catch (IOException e) {e.printStackTrace(); }
                break;
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket)
    {
        // Create stream controller object and call read method
        // to start reading in a separate thread
       new IO_Stream_Controller_Server().readData();
    }

    public static  BluetoothSocket getSocket() {
        return socket;
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try
        {
            mmServerSocket.close();
        }
        catch (IOException e) { }
    }
}