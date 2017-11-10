package net.mkengineering.testapp.services;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.mkengineering.testapp.R;
import net.mkengineering.testapp.StatusFragment;
import net.mkengineering.testapp.objects.DataResponse;

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

    public CloudManager() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
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

    private void processHttpUpdate() {
        try {
            String sURL = "http://ryandel.selfhost.me:8801/vehicle/WP0ZZZ94427/temperature_inside/history";

            // Connect to the URL using java's native library
            URL url = new URL(sURL);
            LastConnectionRequester requester = new LastConnectionRequester();
            requester.execute(url);
        } catch (Exception e) {}
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

    private class LastConnectionRequester extends AsyncTask<URL, Long, Boolean> {

        @Override
        protected Boolean doInBackground(URL... params) {
            try {
                URL url = params[0];
                    //String sURL = url.toString()"http://ryandel.selfhost.me:8801/vehicle/WP0ZZZ94427/temperature_inside/history"; //just a string

                    // Connect to the URL using java's native library
                    //URL url = new URL(sURL);
                    HttpURLConnection request = (HttpURLConnection) url.openConnection();
                    request.connect();


                    ObjectMapper mapper = new ObjectMapper();
                    String response = IOUtils.toString((InputStream) request.getContent(), "UTF-8");

                    StatusFragment.GraphType type = null;
                    if(url.toString().contains("inside")) type = StatusFragment.GraphType.INSIDE;
                    else if(url.toString().contains("outside")) type = StatusFragment.GraphType.OUTSIDE;
                    else type = StatusFragment.GraphType.ENGINE;

                    request.disconnect();
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Boolean result) {
            connected = result;
        }
    }


}
