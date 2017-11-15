package net.mkengineering.testapp.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import net.mkengineering.testapp.R;
import net.mkengineering.testapp.Temperature;
import net.mkengineering.testapp.objects.BluetoothWrapper;

import java.util.Set;

import lombok.SneakyThrows;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by MalteChristjan on 08.11.2017.
 */

public class BluetoothManager implements WirelessConnection{

    private static BluetoothAdapter mBluetoothAdapter;

    private static boolean initialized = false;

    static boolean connected = false;

    private BluetoothWrapper bt;

    public BluetoothManager() {
        BluetoothManager.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BluetoothManager.mBluetoothAdapter != null && !initialized) {
            initialized = true;

            bt = new BluetoothWrapper(Temperature.getInstance());
            bt.setCommunicationCallback(new Callback());
            bt.connectToName(ConfigurationService.getVIN());

            Thread retryThread = new Thread() {

                @Override
                public void run() {
                    retryBluetooth();
                }
            };
            retryThread.start();
        }
    }

    @SneakyThrows
    private void retryBluetooth() {
        while (!BluetoothManager.connected) {
            Log.d("info", "Retry Bluetooth Connection...");
            SECONDS.sleep(3);
            bt.connectToName(ConfigurationService.getVIN());
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

    @Override
    public String getFriendlyName() {
        return "bluetooth";
    }

    @Override
    public int getViewId() {
        return R.id.bluetooth;
    }

    private class Callback implements BluetoothWrapper.CommunicationCallback {

        private Boolean receivedBeacon = false;

        @Override
        @SneakyThrows
        public void onConnect(BluetoothDevice device) {
            System.out.println("Connected");
            BluetoothManager.connected = true;
            while (!receivedBeacon) {
                bt.send("A_C2C_A");
                SECONDS.sleep(1);
            }
        }

        @Override
        public void onDisconnect(BluetoothDevice device, String message) {
            System.out.println("Disconnected");
            BluetoothManager.connected = false;
            receivedBeacon = false;
            retryBluetooth();
        }

        @Override
        public void onMessage(String message) {
            System.out.println("Message " + message);
            if (message.equalsIgnoreCase("A_C2C_T")) {
                receivedBeacon = true;
            }
        }

        @Override
        public void onError(String message) {
            System.out.println("Error" + message);
        }

        @Override
        public void onConnectError(BluetoothDevice device, String message) {
            System.out.println();
        }
    }
}
