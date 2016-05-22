package com.house.sora.btproject.Client;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.house.sora.btproject.Server.BluetoothAsServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by SoRa on 23/5/2016.
 */
public class IO_Stream_Controller_Client
{
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
            if(is != null ) Log.d("IO_CLIENT","Job Well Done");
        }
        else Log.d("IO_Client","Socket is empty");
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
        else Log.d("IO_Client","InputStream is empty");
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
        else Log.d("IO_Client","OutputStream is empty");
        return os;
    }
}
