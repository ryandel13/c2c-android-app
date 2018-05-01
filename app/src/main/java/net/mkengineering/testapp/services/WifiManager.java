package net.mkengineering.testapp.services;

import android.os.Message;
import android.util.Log;

import net.mkengineering.testapp.ConnectionState;
import net.mkengineering.testapp.R;
import net.mkengineering.testapp.Temperature;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by MalteChristjan on 08.11.2017.
 */

public class WifiManager implements WirelessConnection {

    private static Boolean initialized = false;

    public WifiManager() {
        if (!initialized) {
            Log.d("info", "Starting instance of WiFi Update Thread");
            initialized = true;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Log.d("WiFi", "Update WiFi Connection");
                            SECONDS.sleep(1);
                            transferConnectionState();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
        }
    }

    private void transferConnectionState() {
        WirelessConnection.StateMessage message = new WirelessConnection
                .StateMessage();
        message.setExecutor(this);
        message.setConnected(false);

        String ssid = Temperature.getCurrentSsid();
        if (ssid != null) {
            ssid = ssid.substring(1, ssid.length() - 1);
            if (ssid.equalsIgnoreCase(ConfigurationService.getTCUSSID())) {
                message.setConnected(true);
            }
        }
        Message uiMessage = ConnectionState.getmHandler().obtainMessage(1, message);
        ConnectionState.getmHandler().sendMessage(uiMessage);
    }

    public Boolean isConnected() {
        String ssid = Temperature.getCurrentSsid();
        if(ssid != null) {
            ssid = ssid.substring(1,ssid.length()-1);
             if(ssid.equalsIgnoreCase(ConfigurationService.getTCUSSID())) {
                 return true;
             }
        }
        return false;
    }

    public String getFriendlyName() {
        return "wifi";
    }

    @Override
    public int getViewId() {
        return R.id.wifi;
    }
}
