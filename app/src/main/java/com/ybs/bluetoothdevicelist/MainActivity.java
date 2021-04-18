package com.ybs.bluetoothdevicelist;



import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity<NewDevicesArrayAdapter> extends AppCompatActivity {
    //Initialize variable
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    ArrayList<String> stringsArrList = new ArrayList<String>();
    ArrayAdapter<String>  arrayAdapter;
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        listView = (ListView)findViewById(R.id.ListView);
        adapter.startDiscovery();

        //Check permissions for Bluetooth
        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //Initialize Bluetooth device search and call BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver, intentFilter);

        //Enter the names and addresses of the devices found in the list
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, stringsArrList);
        listView.setAdapter(arrayAdapter);

        //A bonus I added to refresh the app to scan more devices
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(myReceiver, intentFilter);
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, stringsArrList);
                listView.setAdapter(arrayAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //If the list is empty you will add all the devices
                if (stringsArrList.size() < 1) {
                    stringsArrList.add(device.getName() + "\n" + device.getAddress());
                    arrayAdapter.notifyDataSetChanged();
                }
                else {  //Checks if the device is already on the list to avoid duplication
                    boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                    for (int i = 0; i < stringsArrList.size(); i++) {
                        if ((device.getName() + "\n" + device.getAddress()).equals(stringsArrList.get(i))) {
                            flag = false;
                        }
                    }
                    if (flag == true) { //The device is not in the list and therefore added to the list
                        stringsArrList.add(device.getName() + "\n" + device.getAddress());
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // unregister the receiver
        unregisterReceiver(myReceiver);
    }
}


//public class MainActivity<NewDevicesArrayAdapter> extends AppCompatActivity {
//    //Initialize variable
//    TextView textView1;
//    private static final int REQUEST_ENABLE_BT = 1;
//    ListView listView;
//    BluetoothAdapter adapter;
//    BluetoothManager manager;
//    List saveList;
//    Set<BluetoothDevice> pairDevices;
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        //Assign variable
//        listView = (ListView) findViewById(R.id.ListView);
//        manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
//        adapter = manager.getAdapter();
//        saveList = new ArrayList();
//        pairDevices = adapter.getBondedDevices(); //Get list of paired device
//        textView1 = (TextView) findViewById(R.id.textView1);
//        textView1.setText("List of paired devices");
//
//        //Check permissions for Bluetooth
//        if (!adapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//        CheckBluetoothState(); //A function that checks the list of paired devices
//    }
//
//
//    private void CheckBluetoothState(){
//        if (adapter == null){
//            textView1.append("\nBluetooth NOT supported. Aborting. ");
//            return;
//        }else {
//            for (BluetoothDevice device : pairDevices){ //For each paired device we will present the name and address to the list
//                String fullDevice = device.getName() + ", " + device.getAddress();
//                saveList.add(fullDevice);
//            }
//            Adapter adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, saveList);
//            listView.setAdapter((ListAdapter)adapter1);
//
//        }
//    }
//}
