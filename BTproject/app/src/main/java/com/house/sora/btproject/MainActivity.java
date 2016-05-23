package com.house.sora.btproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.house.sora.btproject.ASYNC_TASKS.readIncomingData_ASYNC;
import com.house.sora.btproject.Client.BluetoothAsClient;
import com.house.sora.btproject.Client.IO_Stream_Controller_Client;
import com.house.sora.btproject.Server.BluetoothAsServer;
import com.house.sora.btproject.Server.IO_Stream_Controller_Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG";
    private static final int REQUEST_ENABLE_BT = 1453;
    private Button btnFindDevices,btnSendData;
    private BluetoothAdapter btAdapter;
    private ArrayList<String> devicesList;
    private BroadcastReceiver mReceiver;
    private AlertDialog.Builder builderSingle;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<BluetoothDevice> btDeviceList;
    private EditText txtDataToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        setUpBluetooth();
        dialogSetUp();
        registerBroadCastReceiver();

    }

    private void setUpBluetooth()
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null)
        {
            if(!btAdapter.isEnabled())
            {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent,REQUEST_ENABLE_BT);
            }
        }
        else Log.d(TAG,"ggbb");

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
        devicesList = new ArrayList<>();
        builderSingle = new AlertDialog.Builder(MainActivity.this);
        arrayAdapter = new ArrayAdapter<String>( MainActivity.this, android.R.layout.select_dialog_singlechoice,devicesList);
        btDeviceList = new  ArrayList<BluetoothDevice>();
        txtDataToSend = (EditText) findViewById(R.id.txtDataToSend);

    }

    private void sendDataButtonClicked(View v)
    {
        IO_Stream_Controller_Client client = new IO_Stream_Controller_Client();
        IO_Stream_Controller_Server server = new IO_Stream_Controller_Server();

        InputStream client_input = client.getInpoutStream();
        OutputStream client_output = client.getOutputStream();


        Log.d("SendDataClick","So Far So good");
        if(client_input != null)
        {
            Log.d("Client_INPUT","gg");
        }
        if(client_output != null )
        {
            Log.d("Client_OUTPUT","gg");
            OutputStreamWriter writer = new OutputStreamWriter(client_output);
            String charr = String.valueOf(txtDataToSend.getText());
            try {
                writer.write(charr);
                writer.flush();
                Log.d("Client_OUTPUT","data send:"+charr);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        InputStream server_input = server.getInpoutStream();
        OutputStream server_output = server.getOutputStream();

        if(server_input != null)
        {
            Log.d("Server_INPUT","gg");

        }
        if(server_output != null)
        {
            Log.d("Server_OUTPUT","gg");
        }
    }


    private void btnFindDevicesClicked()
    {

        builderSingle.show();

        btAdapter.startDiscovery();






    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK)
        {
            Log.d(TAG,"Well Played");
        }
        else Log.d(TAG,"Not Played");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
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
                        BluetoothAsServer bluetoothAsServer = new BluetoothAsServer();
                        BluetoothAsClient bluetoothAsClient = new BluetoothAsClient(btDeviceList.get(0));
                        bluetoothAsClient.start();
                        bluetoothAsServer.start();

                        btAdapter.cancelDiscovery();
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
                    Log.d(TAG,"added\n");
                    Log.d(TAG,device.getName()+"\n"+device.getAddress());
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    public static void startIncoming(BluetoothSocket mmSocket) {
        new readIncomingData_ASYNC(mmSocket).start();
    }
}
