package com.house.sora.btproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.house.sora.btproject.Controllers.Client.BluetoothAsClient;
import com.house.sora.btproject.Controllers.Client.IO_Stream_Controller_Client;
import com.house.sora.btproject.Controllers.Server.BluetoothAsServer;
import com.house.sora.btproject.Controllers.Server.IO_Stream_Controller_Server;
import com.house.sora.btproject.Util.Constants;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static  int CONNECTED_AS = -1;
    private static boolean SERVER_TEST = false;

    private Button btnFindDevices,btnSendData,btnCheckConnectivity,btnDisconnect,btnGetVisible,btnConnect;
    private TextView txtDataReceived;
    private EditText txtDataToSend;
    private BluetoothAdapter btAdapter;
    private ArrayList<String> devicesList;
    private BroadcastReceiver mReceiver;
    private AlertDialog.Builder builderSingle;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<BluetoothDevice> btDeviceList;
    public static  Handler myHandler ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        setUpBluetooth();
        dialogSetUp();
        registerBroadCastReceiver();

        BluetoothAsServer bluetoothAsServer = new BluetoothAsServer(myHandler);
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
        btnFindDevices = (Button) findViewById(R.id.btnFind);
        btnFindDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFindDevicesClicked();
            }
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
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDisconnectClicked();
            }
        });
        btnGetVisible = (Button) findViewById(R.id.btnGetVisible);
        btnGetVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGetVisibleClicked(v);
            }
        });
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConnectClicked(v);
            }
        });
        txtDataReceived = (TextView) findViewById(R.id.txtDataReceived);
        txtDataToSend = (EditText) findViewById(R.id.txtDataToSend);
        devicesList = new ArrayList<>();
        builderSingle = new AlertDialog.Builder(MainActivity.this);
        arrayAdapter = new ArrayAdapter<String>( MainActivity.this, android.R.layout.select_dialog_singlechoice,devicesList);
        btDeviceList = new  ArrayList<BluetoothDevice>();
        myHandler =  new Handler()
        {
             public void handleMessage(Message msg)
             {
                final int what = msg.getData().getInt(Constants.WHAT);
                switch(what) {
                    case Constants.DO_UPDATE_TEXT:
                    {
                        Bundle b = msg.getData();
                        String next = String.valueOf(b.getChar(Constants.CHAR));
                        Log.d(TAG,"MessageReceived:"+next);
                        doUpdate(msg);
                        Log.d(TAG,"Handler message received");
                    } break;
                    case Constants.CONNECTION_ESTABLISHED_AS_CLIENT:
                    {
                        Toast.makeText(getApplicationContext(),"Connection Established as Client.",Toast.LENGTH_LONG).show();
                        //Listen for data
                        //Write data at button with appropriate stream
                        CONNECTED_AS = Constants.CONNECTION_ESTABLISHED_AS_CLIENT;
                        SERVER_TEST = true;
                    }
                    case Constants.CONNECTION_ESTABLISHED_AS_SERVER:
                    {
                        Toast.makeText(getApplicationContext(),"Connection Established as Server.",Toast.LENGTH_LONG).show();
                        //Listen for data
                        //Write data at button with appropriate stream
                        CONNECTED_AS = Constants.CONNECTION_ESTABLISHED_AS_SERVER;
                    }
                }
            }};
    }

    private synchronized void doUpdate(Message msg)
    {
        // Get the bundle with
        // the data from the Message
        // and update TextView
        Bundle b = msg.getData();
        String previous = (String) txtDataReceived.getText();
        String next = String.valueOf(b.getChar(Constants.CHAR));
        txtDataReceived.setText(previous+next);

    }

    private void btnConnectClicked(View v)
    {
        // Establish the connection.
        BluetoothAsClient bluetoothAsClient = new BluetoothAsClient(btDeviceList.get(0),myHandler);
        bluetoothAsClient.start();
    }

    private void btnGetVisibleClicked(View v) {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(enableBTIntent,Constants.REQUEST_DISCOVERABILITY_BT);
    }

    private void btnDisconnectClicked()
    {
        // Close the connection.
        BluetoothAsClient.cancel();
    }

    private void btnCheckConnectivityClicked()
    {
        try
        {
            String str = String.valueOf(BluetoothAsClient.getSocket().isConnected());
            Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        }
        catch(NullPointerException e){Log.e(TAG,"Null point on check state");}
    }

    private void sendDataButtonClicked(View v)
    {
        switch (CONNECTED_AS)
        {
            case Constants.CONNECTION_ESTABLISHED_AS_CLIENT:
            {
                IO_Stream_Controller_Client client = new IO_Stream_Controller_Client();
                client.sendData(String.valueOf(txtDataToSend.getText()));
            }
            break;
            case Constants.CONNECTION_ESTABLISHED_AS_SERVER:
            {
                // There is a bug if connected as a client
                if(SERVER_TEST)
                {
                    IO_Stream_Controller_Client client = new IO_Stream_Controller_Client();
                    client.sendData(String.valueOf(txtDataToSend.getText()));
                }
                else
                {
                    IO_Stream_Controller_Server server = new IO_Stream_Controller_Server();
                    server.sendData(String.valueOf(txtDataToSend.getText()));
                }
            }
            break;
        }
    }


    private void btnFindDevicesClicked()
    {
        // Show the dialog and begin device discovery
        builderSingle.show();
        btAdapter.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_ENABLE_BT && resultCode == RESULT_OK)
        {
            Log.d(TAG,"Bluetooth enabled");
        }
        if(requestCode == Constants.REQUEST_DISCOVERABILITY_BT && resultCode == RESULT_OK)
        {
            Log.d(TAG,"Bluetooth discoverable");
        }
    }




    public void dialogSetUp()
    {

        builderSingle.setTitle("Select One Device");

        builderSingle.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String strName = arrayAdapter.getItem(which);
                        Toast.makeText(getApplicationContext(),"You clicked "+strName,Toast.LENGTH_LONG).show();

                    }
                });
    }


    public void registerBroadCastReceiver()
    {
        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver= new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    if(!devicesList.contains(device.getName() + "\n/" + device.getAddress()))
                    {
                        devicesList.add(device.getName() + "\n" + device.getAddress());
                        btDeviceList.add(device);
                    }
                    arrayAdapter.notifyDataSetChanged();
                    Log.d(TAG,"Added Device to the List\n");
                    Log.d(TAG,device.getName()+"\n"+device.getAddress());
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

}
