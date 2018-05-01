package net.mkengineering.testapp.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Message;
import android.util.Log;

import net.mkengineering.testapp.ConnectionState;
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

    static boolean connected = false;
    private static BluetoothAdapter mBluetoothAdapter;
    private static boolean initialized = false;
    private BluetoothWrapper bt;

    public BluetoothManager() {
        BluetoothManager.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BluetoothManager.mBluetoothAdapter != null && !initialized) {
            initialized = true;

            bt = new BluetoothWrapper(Temperature.getInstance());
            bt.setCommunicationCallback(new Callback(this));
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
            Log.d("Bluetooth", "Update Bluetooth Connection");
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
        if(this.getAdapter().isEnabled()) {
            Set<BluetoothDevice> devices = this.getAdapter().getBondedDevices();

            for(BluetoothDevice device : devices) {
               if(ConfigurationService.getVIN().equalsIgnoreCase(device.getName())) {
                  return BluetoothManager.connected;
               }
            }

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

        private WirelessConnection wConn;

        public Callback(WirelessConnection wConn) {
            this.wConn = wConn;
        }

        @Override
        @SneakyThrows
        public void onConnect(BluetoothDevice device) {
            Log.d("Bluetooth", "Connection established");
            if (device.getName().equalsIgnoreCase(ConfigurationService.getVIN())) {
                BluetoothManager.connected = true;

                WirelessConnection.StateMessage stateMessage = new StateMessage();
                stateMessage.setExecutor(wConn);
                stateMessage.setConnected(true);
                Message uiMessage = ConnectionState.getmHandler()
                        .obtainMessage(1, stateMessage);
                ConnectionState.getmHandler().sendMessage(uiMessage);
            }
            //while (!receivedBeacon) {
            bt.send("A_C2C_A");
            //    SECONDS.sleep(1);
            //}
        }

        @Override
        public void onDisconnect(BluetoothDevice device, String message) {
            Log.d("Bluetooth", "Connection lost");
            BluetoothManager.connected = false;

            WirelessConnection.StateMessage stateMessage = new StateMessage();
            stateMessage.setExecutor(wConn);
            stateMessage.setConnected(BluetoothManager.connected);
            Message uiMessage = ConnectionState.getmHandler()
                    .obtainMessage(1, stateMessage);
            ConnectionState.getmHandler().sendMessage(uiMessage);

            receivedBeacon = false;
            retryBluetooth();
        }

        @Override
        public void onMessage(String message) {
            Log.i("Bluetooth: ", message);
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
