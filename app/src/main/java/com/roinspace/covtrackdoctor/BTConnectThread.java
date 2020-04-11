/*
 * CovTrack - an app logging Bluetooth devices in your vicinity to monitor infection progress of COVID-19
 * Copyright (C) 2020  Romanian InSpace Engineering

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

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
