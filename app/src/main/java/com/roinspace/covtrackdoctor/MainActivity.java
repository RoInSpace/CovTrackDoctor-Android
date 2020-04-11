package com.roinspace.covtrackdoctor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /*
     * TODO Implement bluetooth pairing
     * TODO Implement RecyclerView Button
     * TODO Implement Firebase
     */



    final static int BLUETOOTH_ADMIN_PERMISSION = 42;
    final static int LOCATION_FINE_PERMISSION   = 43;
    final static int REQUEST_ENABLE_BLUETOOTH   = 40;
    final static int BLUETOOTH_PERMISSION       = 41;
    final static int LOCATION_PERMISSION        = 44;

    private HashMap<BluetoothDevice, Integer> mDevices;
    private ArrayList<BluetoothDevice>  mDevicesSet;
    private BroadcastReceiver           mReceiver;
    private BluetoothAdapter            mBluetoothAdapter;
    private TextView                    statusView;
    private ImageButton testButton;
    private boolean                     isBluetoothEnabled = false;
    private boolean                     isDiscoveryRunning = false;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<String> listPermissions = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        initializeBT();
        initializeUI();



    }

    @Override
    protected void onResume() {

        super.onResume();

        if(isBluetoothServiceRunning("com.roinspace.covtrack.BluetoothService"))
        {
            Log.d("MainActivity","STOP COVTRACKAPP");
            displayStopBluetoothService();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

    }

    private boolean isBluetoothServiceRunning(String serviceName){

        boolean serviceRunning  = false;
        ActivityManager am      = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> l      = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> it = l.iterator();

        while (it.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = it
                    .next();

            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;

                if(runningServiceInfo.foreground)
                {
                    //service run in foreground
                }
            }
        }
        return serviceRunning;
    }

    private void initializeBT() {
        // Enable Bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if device supports Bluetooth
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }

        checkBluetooth();

        mDevices = new HashMap<>();

        // Get Bluetooth devices
        mDevicesSet = new ArrayList<>();

        // Create Broadcast Receiver
        mReceiver = new BluetoothScannerReceiver();

        // Register Broadcast Receiver to scan for bluetooth devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    private void initializeUI() {
        statusView = (TextView) findViewById(R.id.statusView);

        // Create recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.devicesRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify adapter
        mAdapter = new DevicesListAdapter(mDevicesSet,this);
        mRecyclerView.setAdapter(mAdapter);

        // Scan Button
        testButton = (ImageButton) findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDiscoveryRunning) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                else {
                    mDevices.clear();
                    mDevicesSet.clear();
                    // specify adapter

                    mRecyclerView.setAdapter(new DevicesListAdapter(mDevicesSet, getApplicationContext()));

                    checkLocationAvailability();
                    checkBluetooth();
                    mBluetoothAdapter.startDiscovery();
                }
            }
        });
    }

    private void checkPermissions() {
        /*

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_FINE_PERMISSION);
        }
        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH}, BLUETOOTH_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_ADMIN}, BLUETOOTH_ADMIN_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_FINE_PERMISSION);
        }
    }*/


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            listPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            listPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            listPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                listPermissions.add(Manifest.permission.FOREGROUND_SERVICE);
            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                listPermissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (!listPermissions.isEmpty()) {
                requestPermissions(listPermissions.toArray(new String[listPermissions.size()]), LOCATION_PERMISSION);

            }
        }
    }


    private void checkBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == RESULT_OK) {
            isBluetoothEnabled = true;
            Log.d("DEBUG", "Bluetooth is now enabled");
        }
        else if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == RESULT_CANCELED){
            isBluetoothEnabled = false;
            Log.d("DEBUG", "Failed to activate bluetooth");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private class BluetoothScannerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                testButton.setImageResource(R.drawable.scan_btn_selector_en);
                statusView.setText("Not scanning");
                isDiscoveryRunning = false;
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                testButton.setImageResource(R.drawable.scanning_btn_selector_en);
                statusView.setText("Scanning");
                isDiscoveryRunning = true;
            }

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String RSSI = (intent.getExtras()).get(BluetoothDevice.EXTRA_RSSI).toString();

                if(!mDevices.containsKey(device))
                    mDevices.put(device, Integer.parseInt(RSSI));

                mDevices = sortByValues(mDevices);
                mDevicesSet = new ArrayList<>(mDevices.keySet());

                Log.d("DEBUG", device.getName() + " -> " + device.getAddress() + " -> " + RSSI);
                mRecyclerView.setAdapter(new DevicesListAdapter(mDevicesSet,context));
            }

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                   Log.d("PAIR:", "Paired");

                       FirebaseUtil.Patient patient = new FirebaseUtil.Patient(device.getAddress());
                       FirebaseUtil.addPatient(patient);
                       displayPatientAddedConfirmed();
                       BluetoothPair.unpairDevice(device);

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    Log.d("PAIR:", "Unpaired");
                }
            }
        }
    }


    private static HashMap sortByValues(HashMap map) {

        List list = new LinkedList(map.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o2, Object o1) { // switch the order (o1,o2) -> asc | (o2,o1) -> desc
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }


    void checkLocationAvailability() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            displayRequestLocationEnable();
        }
    }

     void displayStopBluetoothService() {
        TextView textView = new TextView(this);
        textView.setText("CovTrack Citizen is running in background");
        textView.setPadding(40, 30, 20, 30);
        textView.setTextSize(17F);
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The application might misbehave if CovTrackCitizen is running in background. Do you want to stop it? ")
                .setCustomTitle(textView)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.roinspace.covtrack");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    void displayRequestLocationEnable() {
        TextView textView = new TextView(this);
        textView.setText("App needs location");
        textView.setPadding(40, 30, 20, 30);
        textView.setTextSize(17F);
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable location")
                .setCustomTitle(textView)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    void displayPatientAddedConfirmed() {
        TextView textView = new TextView(this);
        textView.setText("Patient added");
        textView.setPadding(40, 30, 20, 30);
        textView.setTextSize(17F);
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);

        new AlertDialog.Builder(this)
                .setCustomTitle(textView)
                .setMessage("The Patient was added in authorities database")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
