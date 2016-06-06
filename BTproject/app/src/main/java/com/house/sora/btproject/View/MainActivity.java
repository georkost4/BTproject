package com.house.sora.btproject.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.house.sora.btproject.Controllers.Client.BluetoothAsClient;
import com.house.sora.btproject.Controllers.Client.IO_Stream_Controller_Client;
import com.house.sora.btproject.Controllers.Server.BluetoothAsServer;
import com.house.sora.btproject.Controllers.Server.IO_Stream_Controller_Server;
import com.house.sora.btproject.Controllers.chatMessageHandler;
import com.house.sora.btproject.Model.CustomString;
import com.house.sora.btproject.R;
import com.house.sora.btproject.Util.ChatArrayAdapter;
import com.house.sora.btproject.Util.Constants;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private Button btnFindDevices,btnSendData,btnCheckConnectivity,btnGetVisible;
    private EditText txtDataToSend;
    private BluetoothAdapter btAdapter;
    private ArrayList<CustomString> chatList;
    private ChatArrayAdapter customAdapter;
    public static  Handler myHandler ;
    private static ListView chatListView;
    private static BluetoothAsServer bluetoothAsServer ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        setUpBluetooth();
        startListeningForConnectionsAsServer();


        Log.e(TAG,"----On  Create------");
    }

    private void startListeningForConnectionsAsServer() {
        bluetoothAsServer = new BluetoothAsServer(myHandler);
        bluetoothAsServer.start();
    }

    private void setUpBluetooth()
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null)
        {
            if(!btAdapter.isEnabled())
            {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, Constants.REQUEST_ENABLE_BT);
            }
        }
        else Log.e(TAG,"No Bluetooth action is supported");

    }

    private void initialize() {
        setTitle("Main Panel");
        btnFindDevices = (Button) findViewById(R.id.btnFind);
        btnFindDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivityForResult(new Intent(MainActivity.this, FindDevice.class),Constants.FIND_DEVICE_REQUEST);      }
        });
        btnSendData = (Button) findViewById(R.id.btnSend);
        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataButtonClicked(v);
            }
        });
        btnCheckConnectivity = (Button) findViewById(R.id.btnCheckConnectivity);
        btnCheckConnectivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           btnCheckConnectivityClicked();}   });
        btnGetVisible = (Button) findViewById(R.id.btnGetVisible);
        btnGetVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGetVisibleClicked(v);
            }
        });
        txtDataToSend = (EditText) findViewById(R.id.txtDataToSend);
        chatList = new ArrayList<>();
        customAdapter = new ChatArrayAdapter(this,R.layout.custom_chat_layout,chatList);
        chatListView = (ListView) findViewById(R.id.listViewChat);
        chatListView.setAdapter(customAdapter);
        myHandler =  new chatMessageHandler(MainActivity.this);
    }

    public synchronized void doUpdateChatListView(Message msg)
    {
        // Get the bundle with
        // the data from the Message object
        // and update TextView
        Bundle b = msg.getData();
        String next = b.getString(Constants.CHAR);
        chatList.add(new CustomString(next,Constants.SENDER_DEVICE));
        customAdapter.notifyDataSetChanged();

    }


    private void btnGetVisibleClicked(View v)
    {
        // Start an intent to enable Bluetooth discoverable
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(enableBTIntent,Constants.REQUEST_DISCOVERABILITY_BT);
    }


    private void btnCheckConnectivityClicked()
    {
        try
        {
            switch(Constants.CONNECTED_AS)
            {
                case Constants.CONNECTION_ESTABLISHED_AS_CLIENT:
                {
                    String str = String.valueOf(BluetoothAsClient.getSocket().isConnected());
                    Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                    break;
                }
                case Constants.CONNECTION_ESTABLISHED_AS_SERVER:
                {

                    String str = String.valueOf(BluetoothAsServer.getSocket().isConnected());
                    Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
        catch(NullPointerException e){Log.e(TAG,"Null point on check state");}
    }

    private void sendDataButtonClicked(View v)
    {
        switch (Constants.CONNECTED_AS)
        {
            case Constants.CONNECTION_ESTABLISHED_AS_CLIENT:
            {
                // Create a IO_Stream_Controller_Client object and call sendData method to send a message
                IO_Stream_Controller_Client client = new IO_Stream_Controller_Client();
                client.sendData(String.valueOf(txtDataToSend.getText()));

                // Add the message to the arrayList and notify adapter that changes has been made
                chatList.add(new CustomString(String.valueOf(txtDataToSend.getText()),Constants.SENDER_ME));
                customAdapter.notifyDataSetChanged();
            }
            break;
            case Constants.CONNECTION_ESTABLISHED_AS_SERVER:
            {
                // Create a IO_Stream_Controller_Server object and call sendData method to send a message
                IO_Stream_Controller_Server server = new IO_Stream_Controller_Server();
                server.sendData(String.valueOf(txtDataToSend.getText()));
                // Add the message to the arrayList and notify adapter that changes has been made
                chatList.add(new CustomString(String.valueOf(txtDataToSend.getText()),Constants.SENDER_ME));
                customAdapter.notifyDataSetChanged();

            }
            break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case  Constants.REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK)
                {
                    Log.d(TAG,"Bluetooth enabled");
                }
                break;
            case Constants.REQUEST_DISCOVERABILITY_BT:
                if(resultCode == RESULT_OK)  Log.d(TAG,"Bluetooth discoverable");
                break;
            case Constants.FIND_DEVICE_REQUEST:
                if(resultCode == RESULT_OK)
                {
                    // Get the device from the extras and
                    // start the connection as a client
                    BluetoothDevice  deviceReturned = data.getExtras().getParcelable("Device");
                    BluetoothAsClient bluetoothAsClient = new BluetoothAsClient(deviceReturned,myHandler);
                    bluetoothAsClient.start();
                    Log.d(TAG,"Result received:"+deviceReturned.getAddress());
                }
                break;
        }
    }
}
