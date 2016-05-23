package com.house.sora.btproject.Client;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.house.sora.btproject.ASYNC_TASKS.readIncomingData_ASYNC;
import com.house.sora.btproject.Server.BluetoothAsServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by SoRa on 23/5/2016.
 */
public class IO_Stream_Controller_Client
{
    private static final String TAG = "IO_CLIENT";
    private BluetoothSocket socket = null ;

    public IO_Stream_Controller_Client()
    {
        if(BluetoothAsClient.getSocket()!= null)
        {
            socket = BluetoothAsClient.getSocket();

            InputStream is = null;
            try {
                 is = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(is != null ) Log.d(TAG,"Got the socket...");
        }
        else Log.d(TAG,"Socket is empty");
    }


    public InputStream getInpoutStream()
    {
        InputStream is = null;
        if( socket != null)
        {
            try
            {
                is = socket.getInputStream();
            }
            catch (IOException e) { e.printStackTrace(); }
        }
        else Log.d(TAG,"InputStream is empty");
        return  is;
    }
    public OutputStream getOutputStream()
    {
        OutputStream os = null;
        if(socket != null)
        {
            try
            {
                os = socket.getOutputStream();
            }
            catch (IOException e) {   e.printStackTrace(); }
        }
        else Log.d(TAG,"OutputStream is empty");
        return os;
    }

    public void sendData()
    {
        //Get Bluetooth output stream
        OutputStream os = this.getOutputStream();

        try
        {
            os.write(15);
            Log.d(TAG,"Data send");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.d(TAG,"Transmission failed");
        }
    }

    public void readData()
    {
        if(socket != null)   new readIncomingData_ASYNC(this.socket).start();
        else Log.d(TAG,"could not read the socket cause its empty");
    }
}
