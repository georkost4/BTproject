package com.house.sora.btproject.ASYNC_TASKS;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SoRa on 23/5/2016.
 */
public class readIncomingData_ASYNC extends Thread
{
    private static final String TAG = "readIncomingData_ASync";
    private BluetoothSocket btSocket;
    private InputStream reader;
    private StringBuilder builder;
    public readIncomingData_ASYNC(BluetoothSocket btSocket)
    {
        this.btSocket = btSocket;
        try { reader = btSocket.getInputStream();}
        catch (IOException e) { e.printStackTrace();}
        builder = new StringBuilder();
        Log.d(TAG,"In constructor");
    }


    public void run()
    {
        Log.d(TAG,"Starting the procedure");
        if(btSocket.isConnected())
        {
            try
            {
                int ch = -999;
                Log.d(TAG,"Preparing to start reading...");

                if(reader.available()>0) ch = (char) reader.read();
                else Log.d(TAG,"umfal");
                Log.d(TAG,"PreReading...");
                builder.append((char)ch);
                Log.d(TAG,"Reading...");

            }
            catch (IOException e) { e.printStackTrace();}
            Log.d(TAG,"Done Reading...");
            Log.d(TAG,"Read:"+builder.toString());
        }
        else  Log.d(TAG,"Disconnected");

    }


}
