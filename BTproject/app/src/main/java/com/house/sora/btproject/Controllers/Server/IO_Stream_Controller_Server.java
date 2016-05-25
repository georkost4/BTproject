package com.house.sora.btproject.Controllers.Server;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.house.sora.btproject.MainActivity;
import com.house.sora.btproject.Util.Constants;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by SoRa on 23/5/2016.
 */
public class IO_Stream_Controller_Server {
    private static final String TAG = "IO_Server";
    private BluetoothSocket socket = null;

    public IO_Stream_Controller_Server()
    {
        if(BluetoothAsServer.getSocket()!= null)
        {
            socket = BluetoothAsServer.getSocket();
        }
        else Log.e(TAG,"Empty Socket.");
    }


    public void sendData(String s)
    {
        // Get Bluetooth output stream
        if(socket != null)
        {
            OutputStream os = null;
            try
            {
                os = socket.getOutputStream();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.e(TAG,"Could not get scoket outputstream.");
            }

            // Got the outputStream now send data
            byte [] msg = s.getBytes();
            try
            {
                os.write(msg);
                Log.d(TAG,"Sending data...");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.d(TAG,"Transmission failed");
            }
        }
        else Log.e(TAG,"Null socket");

    }

    public void readData()
    {
        // Read data method
        // Create a thread to keep reading for data
        Runnable r = new Runnable()
        {
            @Override
            public void run() {
                while (true)
                {
                    try
                    {
                        char ch;
                        StringBuilder builder = new StringBuilder();
                        while((ch = (char) socket.getInputStream().read()) !='9')
                        {
                            builder.append(ch);
                            Message msg = new Message();
                            Bundle b = new Bundle();
                            b.putChar(Constants.CHAR,ch);
                            b.putInt(Constants.WHAT,Constants.DO_UPDATE_TEXT);
                            Log.d(TAG, String.valueOf(ch));
                            msg.setData(b);
                            MainActivity.myHandler.sendMessage(msg);
                        }

                        Log.d(TAG,"ReceivedAs_Client:"+builder.toString());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        Log.e(TAG,"Error in reading method. _SERVER");
                    }
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }


}
