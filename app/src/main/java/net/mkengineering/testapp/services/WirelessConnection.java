package net.mkengineering.testapp.services;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MalteChristjan on 08.11.2017.
 */

public interface WirelessConnection {

    Boolean isConnected();

    String getFriendlyName();

    int getViewId();

    @Getter
    @Setter
    class StateMessage {
        Boolean connected;
        WirelessConnection executor;
    }
}
