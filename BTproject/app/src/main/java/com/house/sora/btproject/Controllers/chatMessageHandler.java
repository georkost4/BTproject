package com.house.sora.btproject.Controllers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.house.sora.btproject.R;
import com.house.sora.btproject.View.MainActivity;
import com.house.sora.btproject.Util.Constants;

/**
 * Created by SoRa on 6/6/2016.
 */
public class chatMessageHandler extends Handler
{
    private static final String TAG = "chatMessageHandler Class";
    private static MainActivity mainActivity = null;


    public chatMessageHandler(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    public void handleMessage(Message msg)
    {
        // Get the char code and
        // decide in a switch loop
        // what to do
        final int what = msg.getData().getInt(Constants.WHAT);
        switch (what) {
            case Constants.DO_UPDATE_TEXT: {
                // Get the bundle with the
                // data sent by the handler
                // and call doUpdateChatListView(msg)
                // to update the UI
                Bundle b = msg.getData();
                String next = b.getString(Constants.CHAR);
                Log.d(TAG, "MessageReceived:" + next);
                mainActivity.doUpdateChatListView(msg);
                Log.d(TAG, "Handler message received");
                break;
            }
            case Constants.CONNECTION_ESTABLISHED_AS_CLIENT:
            {
                Toast.makeText(mainActivity.getApplicationContext(), "Connection Established as Client.", Toast.LENGTH_LONG).show();
                //Listen for data
                //Write data at button with appropriate stream
                Constants.CONNECTED_AS = Constants.CONNECTION_ESTABLISHED_AS_CLIENT;

                //Get device name from bundle
                Bundle b = msg.getData();
                String name = b.getString(Constants.CONNECTED_TO);
                mainActivity.setTitle("Connected to "+ name);

                //Disable find devices button
                mainActivity.findViewById(R.id.btnFind).setEnabled(false);
                mainActivity.findViewById(R.id.btnGetVisible).setEnabled(false);

                //set connected icon to actionbar
                mainActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                mainActivity.getSupportActionBar().setIcon(R.drawable.wifi);

                break;
            }
            case Constants.CONNECTION_ESTABLISHED_AS_SERVER: {
                Toast.makeText(mainActivity.getApplicationContext(), "Connection Established as Server.", Toast.LENGTH_LONG).show();
                //Write data at button with appropriate stream
                Constants.CONNECTED_AS = Constants.CONNECTION_ESTABLISHED_AS_SERVER;

                //Get device name from bundle
                // and set the action title bar name
                Bundle b = msg.getData();
                String name = b.getString(Constants.CONNECTED_TO);
                mainActivity.setTitle("Connected to " + name);

                //Disable find devices button
                mainActivity.findViewById(R.id.btnFind).setEnabled(false);
                mainActivity.findViewById(R.id.btnGetVisible).setEnabled(false);

                //set connected icon to actionbar
                mainActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                mainActivity.getSupportActionBar().setIcon(R.drawable.wifi);
                break;
            }
            case Constants.FAILED_TO_CONNECT:
            {
                // Do things when failed to connect...
                Toast.makeText(mainActivity.getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
                break;
            }
            case Constants.CONNECTION_LOST:
            {
                // Do things when connection is lost...
                Toast.makeText(mainActivity.getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                Constants.CONNECTED_AS = Constants.CONNECTION_LOST;
                // Update the actio bar title to " Main Panel "
                mainActivity.setTitle("Main Panel");
                //Enable find devices button
                mainActivity.findViewById(R.id.btnFind).setEnabled(true);
                mainActivity.findViewById(R.id.btnGetVisible).setEnabled(true);

                //hide connected icon to actionbar
                mainActivity.getSupportActionBar().setDisplayShowHomeEnabled(false);
            }
        }
    }
}
