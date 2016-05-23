package com.house.sora.btproject.ASYNC_TASKS;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by SoRa on 23/5/2016.
 */
public class readIncomingData_ASYNC extends AsyncTask
{
    private static final String TAG = "readIncomingData_ASync";
    private BluetoothSocket btSocket;
    private InputStreamReader reader;
    private StringBuilder builder;
    public readIncomingData_ASYNC(BluetoothSocket btSocket)
    {
        this.btSocket = btSocket;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try { reader = new InputStreamReader(btSocket.getInputStream());}
        catch (IOException e) { e.printStackTrace();}
    }

    @Override
    protected Object doInBackground(Object[] params)
    {
        while(btSocket.isConnected())
        {
            try
            {
                char character;
                while((character = (char) reader.read()) != -1)
                {
                    builder.append(character);
                    Log.d(TAG,"Reading...");
                }
            }
            catch (IOException e) { e.printStackTrace();}
            Log.d(TAG,"Done Reading...");
            Log.d(TAG,"Read:"+builder.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

    }
}
