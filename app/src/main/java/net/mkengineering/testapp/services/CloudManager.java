package net.mkengineering.testapp.services;

import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.mkengineering.studies.vds.DataResponse;
import net.mkengineering.testapp.ConnectionState;
import net.mkengineering.testapp.R;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by MalteChristjan on 08.11.2017.
 */

public class CloudManager implements WirelessConnection {

    private static Boolean connected = false;

    private static Boolean carConnected = false;

    private static Boolean initialized = false;

    public CloudManager() {
        if (!initialized) {
            Log.d("info", "Starting instance of Cloud Update Thread");
            initialized = true;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Log.d("Cloud", "Update Cloud Connection");
                            SECONDS.sleep(1);
                            processHttpUpdate();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
        }
    }

    private void processHttpUpdate() {
        try {
            String sURL = "http://ryandel.selfhost.me:8801/vehicle/"
                    + ConfigurationService.getVIN()
                    + "/lastConnection";

            // Connect to the URL using java's native library
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setReadTimeout(1000);
            request.connect();

            ObjectMapper mapper = new ObjectMapper();
            String response = IOUtils.toString((InputStream) request
                    .getContent(), "UTF-8");

            request.disconnect();
            DataResponse dResp = mapper.readValue(response, DataResponse.class);
            connected = true;
            if (dResp.getTimestamp() < System.currentTimeMillis() - (5 * 60 * 1000)) {
                carConnected = true;
            }


            WirelessConnection.StateMessage message = new WirelessConnection
                    .StateMessage();
            message.setConnected(connected);
            message.setExecutor(this);

            Message x = ConnectionState.getmHandler().obtainMessage(1, message);
            ConnectionState.getmHandler().sendMessage(x);

        } catch (Exception e) {
            Log.d("warn", "Connection to cloud failed");
            connected = false;
        }
    }

    public Boolean isConnected() {
        return connected;
    }

    @Override
    public String getFriendlyName() {
        return "cloud";
    }

    @Override
    public int getViewId() {
        return R.id.cloud;
    }
}
