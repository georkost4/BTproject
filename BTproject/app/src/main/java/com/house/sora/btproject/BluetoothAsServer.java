package com.house.sora.btproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothAsServer extends Thread 
{

    private static final String NAME = "BTproject";
    private static final String MY_UUID = "f49beda8-1f8b-11e6-b6ba-3e1d05defe78";
    private final BluetoothServerSocket mmServerSocket;
    private  BluetoothAdapter mBluetoothAdapter;

    public BluetoothAsServer()
    {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;
        try
        {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, UUID.fromString(MY_UUID));
        }
        catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run()
    {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true)
        {
            try
            {
                socket = mmServerSocket.accept();
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
        try
        {
            InputStream is = socket.getInputStream();
            Log.d("Incoming", String.valueOf(is.read()));
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothServerSocket getMmServerSocket() {
        return mmServerSocket;
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