package com.house.sora.btproject.Util;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.house.sora.btproject.R;

import java.util.ArrayList;

/**
 * Created by SoRa on 3/6/2016.
 */
public class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDevice> {
    ArrayList<BluetoothDevice> devices = null;

    public BluetoothDeviceArrayAdapter(Context context, int resource, ArrayList<BluetoothDevice> list) {
        super(context, resource, list);
        devices = new ArrayList<BluetoothDevice>();
        devices = list ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null)
        {
            // Inflate the view
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(android.R.layout.simple_list_item_single_choice, null);
        }

        TextView textView = (TextView) v.findViewById(android.R.id.text1);
        if(!devices.isEmpty())
        {
            // Get the stock layout from android
            //  textview and set it to the device
            // name
            textView.setText(devices.get(position).getName());
        }
        return v;
    }

}