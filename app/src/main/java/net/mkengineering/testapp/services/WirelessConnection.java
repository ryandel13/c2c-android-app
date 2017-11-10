package net.mkengineering.testapp.services;

/**
 * Created by MalteChristjan on 08.11.2017.
 */

public interface WirelessConnection {

    Boolean isConnected();

    String getFriendlyName();

    int getViewId();

}
