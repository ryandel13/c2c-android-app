package net.mkengineering.testapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.mkengineering.testapp.services.BluetoothManager;
import net.mkengineering.testapp.services.CloudManager;
import net.mkengineering.testapp.services.WifiManager;
import net.mkengineering.testapp.services.WirelessConnection;

import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by MalteChristjan on 08.11.2017.
 */

public class ConnectionState extends Fragment {

    public static ConnectionState getUiObject() {
        return uiObject;
    }

    public enum ConnectionAction {

    BLUETOOTH_AVAILABLE, BLUETOOTH_UNAVAILABLE, WIFI_AVAILABLE, WIFI_UNAVAILABLE,
    CLOUD_AVAILABLE, CLOUD_UNAVAILABLE
}

    private BluetoothManager btManager = new BluetoothManager();
    private WifiManager wfManager = new WifiManager();
    private CloudManager cdManager = new CloudManager();

    private static ConnectionState uiObject;

    public ConnectionState() {
        if(uiObject == null) {
            uiObject = this;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        SECONDS.sleep(1);
                        ConnectionState.getUiObject().retrieveConnectionStates();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.connection_state, container, false);
    }

    private void retrieveConnectionStates() {
        CheckConnectionTask requestTask = new CheckConnectionTask();
        try {
           requestTask.execute(btManager, wfManager,cdManager);
        }
        catch(Exception e){}
    }

    public void updateConnections(WirelessConnection clazz, Boolean state) {
        int visibility = state ? View.VISIBLE : View.INVISIBLE;
        getView().findViewById(clazz.getViewId()).setVisibility(visibility);
    }

    private class CheckConnectionTask extends AsyncTask<WirelessConnection, Integer, Map<WirelessConnection, Boolean>> {

        @Override
        protected Map<WirelessConnection, Boolean> doInBackground(WirelessConnection... params) {
            Map<WirelessConnection, Boolean> out = new HashMap<>();
            for(WirelessConnection wConn : params) {
                out.put(wConn, wConn.isConnected());
            }
            return out;
        }

        protected void onPostExecute(Map<WirelessConnection, Boolean> result) {
            for(WirelessConnection c : result.keySet()) {
                updateConnections(c, result.get(c));
            }
        }
    }

    public static void updateConnectionStatus(ConnectionAction action) {
        switch(action) {
            case BLUETOOTH_AVAILABLE: uiObject.getView().findViewById(R.id.bluetooth).setVisibility(View.VISIBLE);
            break;
            case BLUETOOTH_UNAVAILABLE: uiObject.getView().findViewById(R.id.bluetooth).setVisibility(View.INVISIBLE);
            break;
            case WIFI_AVAILABLE: uiObject.getView().findViewById(R.id.bluetooth).setVisibility(View.VISIBLE);
            break;
            case WIFI_UNAVAILABLE: uiObject.getView().findViewById(R.id.bluetooth).setVisibility(View.INVISIBLE);
            break;
            case CLOUD_AVAILABLE: uiObject.getView().findViewById(R.id.bluetooth).setVisibility(View.VISIBLE);
            break;
            case CLOUD_UNAVAILABLE: uiObject.getView().findViewById(R.id.bluetooth).setVisibility(View.INVISIBLE);
            default: return;
        }
    }
}
