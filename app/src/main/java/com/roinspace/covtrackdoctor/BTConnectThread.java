package com.roinspace.covtrackdoctor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BTConnectThread extends Thread {
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;

    public BTConnectThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        mDevice = device;

        UUID uuid = new UUID(1, 1);

        try {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e("Error", "Socket's create() method failed", e);
        }

        mSocket = tmp;
    }

    public void run() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();

        try {
            mSocket.connect();
        } catch (IOException connectException) {
            try {
                mSocket.close();
            } catch (IOException closeException) {
                Log.e("ERROR", "Error in Socket connect() method", closeException);
            }
            return;
        }

        try {
            mSocket.close();
        } catch (IOException closeException) {
            Log.e("ERROR", "Error in Socket connect() method", closeException);
        }
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException closeException) {
            Log.e("ERROR", "Error in Socket connect() method", closeException);
        }
    }
}
