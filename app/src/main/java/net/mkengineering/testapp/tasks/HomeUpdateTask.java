package net.mkengineering.testapp.tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.mkengineering.testapp.HomeFragment;
import net.mkengineering.testapp.StatusFragment;
import net.mkengineering.testapp.objects.DataResponse;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MalteChristjan on 27.09.2017.
 */

public class HomeUpdateTask extends AsyncTask<URL, Integer, Object> {
    // Do the long-running work in here
    Map<StatusFragment.GraphType, Object> responseMapper = new HashMap<>();

    protected Object doInBackground(URL... urls) {
        try {
            //for(URL url : urls) {
            //String sURL = url.toString()"http://ryandel.selfhost.me:8801/vehicle/WP0ZZZ94427/temperature_inside/history"; //just a string

            // Connect to the URL using java's native library
            //URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) urls[0].openConnection();
            request.connect();


            ObjectMapper mapper = new ObjectMapper();
            String response = IOUtils.toString((InputStream) request.getContent(), "UTF-8");


            request.disconnect();

            return mapper.readValue(response, DataResponse.class);

            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Object result) {
        if (result != null) {
            HomeFragment.getUiObject().updateHomeScreen(result);
        }
    }
}