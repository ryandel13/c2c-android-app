package net.mkengineering.testapp.services;

import net.mkengineering.testapp.objects.Constants;

import java.net.URL;

import lombok.SneakyThrows;

/**
 * Created by MalteChristjan on 17.10.2017.
 */

public class ConfigurationService {

    public static String getUsername() {
        return Constants.username;
    }

    public static String getVIN() {
        return Constants.VIN;
    }

    public static String getTCUSSID() {return Constants.TcuSssid;  }

    @SneakyThrows
    public static URL getRemoteUrl() { return new URL(Constants.remoteBaseUrl); }

}
