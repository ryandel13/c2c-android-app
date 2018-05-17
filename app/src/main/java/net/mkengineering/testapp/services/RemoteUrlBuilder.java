package net.mkengineering.testapp.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by MalteChristjan on 10.11.2017.
 */

public class RemoteUrlBuilder
{
    private static RemoteUrlBuilder instance;

    private static RemoteUrlBuilder getInstance()
    {
        if(instance == null)
        {
            instance = new RemoteUrlBuilder();
        }
        return instance;
    }

    private RemoteUrlBuilder()
    {
        ports.put(SERVICE.VDS, 8801);
        ports.put(SERVICE.SDS, 8808);
        ports.put(SERVICE.CES, 8803);
        ports.put(SERVICE.POI, 8804);
    }
    private Map<SERVICE, Integer> ports = new HashMap<>();

    public enum SERVICE {
        VDS, SDS, CES, POI
    }
    public static URL getUriFor(SERVICE service, String context, String ressource, URL baseUrl)
    {
        int port = 0;
        port = getInstance().ports.get(service);
        String vin = ConfigurationService.getVIN();
        URL url = null;
        try {
            url = new URL(baseUrl.toString() + ":" + port + "/" + context + "/" + vin + "/" + ressource);
        } catch (MalformedURLException mfue) {
            System.out.println("Given URL could not be build.");
        }
        return url;
    }
}