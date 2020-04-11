package com.roinspace.covtrackdoctor;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class DevicesListAdapter extends RecyclerView.Adapter<DevicesListAdapter.DevicesViewHolder> {
    private ArrayList<BluetoothDevice> mDataset;
    private Context context;
    public static class DevicesViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView subtitle;
        public ImageButton connectButton;
        public DevicesViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.list_title);
            subtitle = (TextView) v.findViewById(R.id.list_subtitle);
            connectButton = (ImageButton) v.findViewById(R.id.connect_btn);
        }
    }

    public DevicesListAdapter(ArrayList<BluetoothDevice> dataset,Context context) {
        mDataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public DevicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View deviceView = inflater.inflate(R.layout.list_row, parent, false);

        DevicesViewHolder viewHolder = new DevicesViewHolder(deviceView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesViewHolder holder, final int position) {
        BluetoothDevice btDevice = mDataset.get(position);

        TextView title = holder.title;
        TextView subtitle = holder.subtitle;

        final int MAX_NAME_SIZE = 25;

        if (btDevice.getName() != null)
            title.setText(btDevice.getName().length() > MAX_NAME_SIZE ? btDevice.getName().substring(0, MAX_NAME_SIZE) + ".." : btDevice.getName());
        else
            title.setText("Unnamed device");

        subtitle.setText(btDevice.getAddress());

        ImageButton connectButton = holder.connectButton;

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isNetworkAvailable())
                {
                    displayAlertNoInternet();
                    return;
                }

                BluetoothPair.pairDevice(mDataset.get(position));


            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    void displayAlertNoInternet() {
        TextView textView = new TextView(context);
        textView.setText("No internet connection");
        textView.setPadding(40, 30, 20, 30);
        textView.setTextSize(17F);
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);

        new AlertDialog.Builder(context)
                .setCustomTitle(textView)
                .setMessage("Turn on internet connection.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
