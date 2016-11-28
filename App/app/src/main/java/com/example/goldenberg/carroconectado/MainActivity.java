package com.example.goldenberg.carroconectado;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.goldenberg.carroconectado.Application.CarroConectado;
import com.example.goldenberg.carroconectado.Bluetooth.ConnectActivity;
import com.example.goldenberg.carroconectado.GPS.GPS_Service;
import com.example.goldenberg.carroconectado.controller.BtnHttpCtrl;
import com.example.goldenberg.carroconectado.controller.BtnController;
import com.example.goldenberg.carroconectado.model.Model;
import com.example.goldenberg.carroconectado.view.SettingsView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;
import java.util.UUID;

/**
 * Created by Goldenberg on 27/09/16.
 */
public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice mDevice = null;
    ConnectThread connectThread;
    public ConnectedThread connectedThread;
    public SettingsView settingsView;
    //Dividir em thread
    BluetoothSocket mSocket = null;
    //

    private static final int REQUEST_BLUETOOTH = 1;
    private static final int REQUEST_CONNECTION = 2;

    public boolean connected = false;
    private boolean gpsPermission = true;

    private static String MAC = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Model model = ((CarroConectado) this.getApplication()).getModel();

        settingsView = new SettingsView(findViewById(R.id.settings_view), model);
        BtnController btnController = new BtnController(settingsView, model);
        BtnHttpCtrl btnHttpCtrl = new BtnHttpCtrl(settingsView, model);


        this.startBluetooth();

        if (!runtimePermission())
            gpsPermission = true;

    }

    public void getLocation(String velOBD) {
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        i.putExtra("velOBD", velOBD);
        startService(i);
    }

    private boolean runtimePermission() {
        System.out.println("Debug");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET)!=
                PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 100);
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
                    gpsPermission = true;
                else
                    runtimePermission();
        }
    }

    public void startBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Bluetooth not Supported",Toast.LENGTH_LONG);
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH);
        }
    }

    public void connect(){
        if(mBluetoothAdapter.isEnabled()){
            Intent deviceList = new Intent(MainActivity.this, ConnectActivity.class);
            startActivityForResult(deviceList,REQUEST_CONNECTION);
        }
    }

    public void disconnect(){
        try {
            connectThread.cancel();
            connectedThread.cancel();
            this.connected = false;
        } catch (NullPointerException error){
            Log.getStackTraceString(error);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case (REQUEST_BLUETOOTH):
                if (resultCode == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(),"Bluetooth Activated",Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(getApplicationContext(),"You must enable bluetooth to use this application",Toast.LENGTH_LONG);
                    finish();
                }
                break;
            case (REQUEST_CONNECTION):
                if(resultCode == Activity.RESULT_OK){
                    MAC = data.getExtras().getString(ConnectActivity.MAC_ADDRESS);
                    //Toast.makeText(getApplicationContext(),"MAC Address: " + MAC,Toast.LENGTH_LONG).show();
                    mDevice = mBluetoothAdapter.getRemoteDevice(MAC);

                    connectThread = new ConnectThread(mDevice, mHandler);
                    connectThread.start();
                    this.connected = true;
                } else {
                    Toast.makeText(getApplicationContext(),"Failed to obtain MAC Address",Toast.LENGTH_LONG).show();
                    settingsView.bluetooth_btn.setChecked(false);
                }
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;

            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    //Log.v("Bluetooth",writeMessage);
                    if (gpsPermission)
                        getLocation(writeMessage);
                    //String message = "Oi";
                    //connectedThread.write(message.getBytes());
                    break;
            }
        }
    };

    public synchronized void connectedThread(BluetoothSocket socket, Handler handler){
        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectedThread = new ConnectedThread(socket,handler);
        connectedThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final Handler mHandler;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        public ConnectThread(BluetoothDevice device, Handler handler) {
            BluetoothSocket tmp = null;
            mHandler = handler;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {}
            mmSocket = tmp;
        }
        public void run() {
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {}
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (MainActivity.this) {
                connectThread = null;
            }

            connectedThread(mmSocket,mHandler);
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final Handler mHandler;

        public ConnectedThread(BluetoothSocket socket, Handler handler) {
            mHandler = handler;
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i = begin; i < bytes; i++) {
                        if(buffer[i] == "#".getBytes()[0]) {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                //Log.v("Enviando",bytes.toString());
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.getStackTraceString(e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
