package net.mkengineering.testapp.services;

import net.mkengineering.testapp.R;
import net.mkengineering.testapp.Temperature;

/**
 * Created by MalteChristjan on 08.11.2017.
 */

public class WifiManager implements WirelessConnection {

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
