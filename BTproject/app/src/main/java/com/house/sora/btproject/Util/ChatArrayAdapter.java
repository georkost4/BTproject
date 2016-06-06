package com.house.sora.btproject.Util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.house.sora.btproject.Model.CustomString;
import com.house.sora.btproject.R;

import java.util.ArrayList;

/**
 * Created by SoRa on 3/6/2016.
 */
public class ChatArrayAdapter extends ArrayAdapter<CustomString>
{
    private static final String TAG = "ChatArrayAdapter";
    private ArrayList<CustomString> chatList;
    private int sender;
    public ChatArrayAdapter(Context context, int resource, ArrayList<CustomString> list)
    {
        super(context, resource, list);
        chatList = new ArrayList<>();
        chatList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null)
        {
            // Inflate the view
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.custom_chat_layout, null);
        }


        // Get the textView form custom Layout
        // Get the sender from ArrayList
        TextView textView = (TextView) v.findViewById(R.id.textViewChat);
        sender = chatList.get(position).getWho();


        Log.d(TAG,"Inside Chat Adapter");
        switch (sender)
        {
            case Constants.SENDER_ME:
            {
                //Set the text to the chat listview
                textView.setText(chatList.get(position).getString());
                //change the bg color and the chat icon
                textView.setBackgroundColor(Color.argb(255,255, 153, 102));
                ImageView imageView = (ImageView) v.findViewById(R.id.imageViewChat);
                Drawable myIcon = v.getResources().getDrawable( R.drawable.android_device_ico);
                imageView.setImageDrawable(myIcon);

                break;
            }
            case Constants.SENDER_DEVICE:
            {
                // set the text to the chat listview
                textView.setText(chatList.get(position).getString());
                //change the bg color and the chat icon
                textView.setBackgroundColor(Color.argb(255,51,153,255));
                ImageView imageView = (ImageView) v.findViewById(R.id.imageViewChat);
                Drawable myIcon = v.getResources().getDrawable( R.drawable.bluetooth_device_ico);
                imageView.setImageDrawable(myIcon);
            }

        }
        return v;

    }
}
