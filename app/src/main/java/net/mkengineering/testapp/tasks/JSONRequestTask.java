package net.mkengineering.testapp.tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

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

public class JSONRequestTask extends AsyncTask<URL, Integer, Long> {
    // Do the long-running work in here
   Map<StatusFragment.GraphType, Object> responseMapper = new HashMap<>();

    protected Long doInBackground(URL... urls) {
        try {
            for(URL url : urls) {
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

                responseMapper.put(type, mapper.readValue(response, DataResponse.class));
                request.disconnect();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new Long(1);
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Long result) {
        for(StatusFragment.GraphType type : responseMapper.keySet()) {
            StatusFragment.getUiObject().setJSON(responseMapper.get(type), type);
        }
        //showNotification("Downloaded " + result + " bytes");
    }
}