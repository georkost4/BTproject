package com.house.sora.btproject.Server;

import android.bluetooth.BluetoothSocket;

import com.house.sora.btproject.Client.BluetoothAsClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by SoRa on 23/5/2016.
 */
public class IO_Stream_Controller_Server
{
    private BluetoothSocket socket = null ;

    public IO_Stream_Controller_Server()
    {
        if(BluetoothAsClient.getSocket()!= null)
        {
            socket = BluetoothAsServer.getSocket();
        }
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
        return os;
    }
}
