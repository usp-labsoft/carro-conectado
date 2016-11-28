package com.example.goldenberg.carroconectado.Bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Goldenberg on 28/09/16.
 */
public class ConnectActivity extends ListActivity {

    private BluetoothAdapter mBluetoothAdapter = null;
    public static String MAC_ADDRESS = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                ArrayBluetooth.add(device.getName().toString() + "\n" + device.getAddress().toString());
            }
        } else {
            ArrayBluetooth.add("No Paired Device.");
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String generalInfo = ((TextView) v).getText().toString();
        String macAddr = generalInfo.substring(generalInfo.length() - 17);

        Intent returnMac = new Intent();
        returnMac.putExtra(MAC_ADDRESS,macAddr);
        setResult(RESULT_OK,returnMac);
        finish();

    }
}
