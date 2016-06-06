package com.house.sora.btproject.Controllers.Client;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.house.sora.btproject.View.MainActivity;
import com.house.sora.btproject.Util.Constants;

import java.io.IOException;
import java.io.OutputStream;

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
        }
        else Log.e(TAG,"Client Socket is empty");
    }


    public void sendData(String s) {
        // Get Bluetooth output stream
        if (socket != null) {
            OutputStream os = null;
            try
            {
                os = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Could not get client scoket outputstream.");
            }

            s+='\u0000'; // Add NULL char to  define END OF MESSAGE

            // Got the outputStream now send data
            byte[] msg = s.getBytes();
            try {
                os.write(msg);
                Log.d(TAG, "Sending data...");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Transmission failed");
            }
        } else Log.e(TAG, "Null socket");
    }



    public void readData()
    {
        // Read data method
        //Create a thread to keep reading for data

        Runnable r = new Runnable()
        {
            @Override
            public void run() {
                while (true)
                {
                    try
                    {
                        char ch;
                        StringBuilder message = new StringBuilder();
                        while((ch = (char) socket.getInputStream().read()) !='\u0000') // Read until end of message then Update UI
                        {
                            message.append(ch);
                        }
                        // update the main thread
                        sendMessageToUIThread(Constants.DO_UPDATE_TEXT, message);

                        Log.d(TAG,"ReceivedAs_Client:"+ message.toString());
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG,"Error in reading method. _CLIENT");
                        e.printStackTrace();
                        //Send error message in Main Thread
                        sendMessageToUIThread(Constants.CONNECTION_LOST,null);
                        // Exit the thread
                        break;
                    }
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    private void sendMessageToUIThread(int what, StringBuilder message)
    {
        Message msg = new Message();
        Bundle b = new Bundle();

        switch (what)
        {
            case Constants.DO_UPDATE_TEXT:
            {
                b.putString(Constants.CHAR, message.toString());
                b.putInt(Constants.WHAT,Constants.DO_UPDATE_TEXT);
                msg.setData(b);
                break;
            }
            case Constants.CONNECTION_LOST:
            {
                b.putInt(Constants.WHAT,Constants.CONNECTION_LOST);
                Log.e(TAG, "--IO_STREAM_CLIENT");
                Log.e(TAG, "Sending error message to UI thread...");
                msg.setData(b);
                break;
            }
        }
        MainActivity.myHandler.sendMessage(msg);
    }


}
