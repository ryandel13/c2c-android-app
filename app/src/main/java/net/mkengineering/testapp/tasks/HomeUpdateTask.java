package net.mkengineering.testapp.tasks;

import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.mkengineering.testapp.HomeFragment;
import net.mkengineering.testapp.objects.DataResponse;
import net.mkengineering.testapp.services.CloudManager;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MalteChristjan on 27.09.2017.
 */

public class HomeUpdateTask implements Runnable {

    private Boolean wasUpdated = false;

    @Override
    public void run() {
        while (!wasUpdated) {
            Log.d("HomeFragmentAttributes", "Trying to retrieve Home Attributes");
            retrieveAttributes();
        }
    }

    private void retrieveAttributes() {
        try {
            //HomeUpdateTask hut = new HomeUpdateTask();
            String sURL = "http://ryandel.selfhost.me:8802/vehicle/WP0ZZZ94427/";
            if (!(new CloudManager()).isConnected()) {
                sURL = "http://192.168.0.100:8802/vehicle/WP0ZZZ94427/";
            }
            // Connect to the URL using java's native library
            Log.d("info", "Retrieve vehicle data from " + sURL);
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setConnectTimeout(1000);
            request.connect();


            ObjectMapper mapper = new ObjectMapper();
            String response = IOUtils.toString((InputStream) request.getContent(), "UTF-8");


            request.disconnect();

            DataResponse dR = (DataResponse) mapper.readValue(response, DataResponse.class);
            HomeFragmentMessage hfm = new HomeFragmentMessage();
            hfm.setDataResponse(dR);

            Message uiMessage = HomeFragment.getMHandler().obtainMessage(1, hfm);
            HomeFragment.getMHandler().sendMessage(uiMessage);
            wasUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Getter
    @Setter
    public class HomeFragmentMessage {
        private DataResponse dataResponse;
    }
}