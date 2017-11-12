package net.mkengineering.testapp.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.widget.Toast;

import net.mkengineering.testapp.ConnectionState;
import net.mkengineering.testapp.R;
import net.mkengineering.testapp.StatusFragment;
import net.mkengineering.testapp.Temperature;

import java.util.Set;
import java.util.UUID;

/**
 * Created by MalteChristjan on 08.11.2017.
 */

public class BluetoothManager implements WirelessConnection{

    private static BluetoothAdapter mBluetoothAdapter;

    private static android.bluetooth.BluetoothManager btManager;

    private static boolean initialized = false;

    private static boolean connected = false;

    public BluetoothManager() {
        BluetoothManager.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BluetoothManager.mBluetoothAdapter != null && !initialized) {
            mBluetoothAdapter.disable();
            initialized = true;

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            Temperature.registerReceiverRemote(mReceiver, filter);
        }
    }




    private BluetoothAdapter getAdapter() {
        return BluetoothManager.mBluetoothAdapter;
    }

    /**
     * Checks if connection via Bluetooth to the desired VIN Bluetooth can be granted.
     *
     * @return Connection State
     */
    public Boolean isConnected() {
        BluetoothSocket mSocket = null;

        if(this.getAdapter().isEnabled()) {
            Set<BluetoothDevice> devices = this.getAdapter().getBondedDevices();

            for(BluetoothDevice device : devices) {
               if(ConfigurationService.getVIN().equalsIgnoreCase(device.getName())) {
                  return BluetoothManager.connected;
               }
            }

        } else
        {
            //this.getAdapter().enable();
        }
        return false;
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)
                    && device.getName().equalsIgnoreCase(ConfigurationService.getVIN())) {
                //Do something if connected
                //Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
                BluetoothManager.connected = true;
                ConnectionState.updateConnectionStatus(ConnectionState.ConnectionAction.BLUETOOTH_AVAILABLE);
                Temperature.makeToast("Vehicle is nearby");
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)
                    && device.getName().equalsIgnoreCase(ConfigurationService.getVIN())) {
                //Do something if disconnected
                //Toast.makeText(getApplicationContext(), "BT Disconnected", Toast.LENGTH_SHORT).show();
                BluetoothManager.connected = false;
                ConnectionState.updateConnectionStatus(ConnectionState.ConnectionAction.BLUETOOTH_UNAVAILABLE);
                Temperature.makeToast("Vehicle is out of range");
            }
        }
    };

    @Override
    public String getFriendlyName() {
        return "bluetooth";
    }

    @Override
    public int getViewId() {
        return R.id.bluetooth;
    }
}
