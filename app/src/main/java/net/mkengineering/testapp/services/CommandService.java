package net.mkengineering.testapp.services;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.mkengineering.studies.ces.Command;


import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by MalteChristjan on 17.10.2017.
 */

public class CommandService {

    public enum COMMAND {
        LOCK, UNLOCK, TRUNK
    }

    public void sendCommand(COMMAND command) {
        final Command cmd = new Command();
        cmd.setName(command.name());
        cmd.setUser(ConfigurationService.getUsername());
        cmd.setVin(ConfigurationService.getVIN());
        cmd.setTimestamp(System.currentTimeMillis());
        cmd.setCommandAttribute("");

        Thread thread = new Thread() {
            @Override
            public void run() {

                CommandRequest cReq = new CommandRequest();
                cReq.execute(cmd);

            }
        };

        thread.start();
    }


    public class CommandRequest extends AsyncTask<Command, Integer, Long> {

        @Override
        protected Long doInBackground(Command... commands) {
            for (Command command : commands)
                try {
                    URL url = new URL("http://ryandel.selfhost.me:8803/command/" + ConfigurationService.getVIN() + "/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setDoOutput(true);
                    System.out.println(url.toString());
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());

                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = mapper.writeValueAsString(command);

                    //String output = String.format("{\"value\":%1$s,\"type\":\"%2$s\"}", value, String.class.getName());

                    osw.write(jsonString);
                    osw.flush();
                    osw.close();

                    System.out.println(jsonString);
                    System.err.println(connection.getResponseCode());

                    connection.disconnect();
                } catch (Exception e) {

                }
            return null;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Long result) {
            //showNotification("Downloaded " + result + " bytes");
        }
    }
}
