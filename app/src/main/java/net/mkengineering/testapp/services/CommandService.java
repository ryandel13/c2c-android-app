package net.mkengineering.testapp.services;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.mkengineering.studies.ces.Command;
import net.mkengineering.testapp.objects.Constants;

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

        CommandRequestThread crt = new CommandRequestThread(cmd);

        Thread thread = new Thread(crt); /*{
            @Override
            public void run() {

                CommandRequestThread cReq = new CommandRequestThread();
                cReq.execute(cmd);

            }
        };*/

        thread.start();
    }


    public class CommandRequestThread implements Runnable {

        private Command cmd;

        public CommandRequestThread(Command cmd) {
            this.cmd = cmd;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(Constants.remoteBaseUrl + ":8803/command/" + ConfigurationService.getVIN() + "/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setDoOutput(true);
                connection.setReadTimeout(1000);
                System.out.println(url.toString());
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());

                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(cmd);

                //String output = String.format("{\"value\":%1$s,\"type\":\"%2$s\"}", value, String.class.getName());

                osw.write(jsonString);
                osw.flush();
                osw.close();

                System.out.println(jsonString);
                System.err.println(connection.getResponseCode());

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class CommandRequest extends AsyncTask<Command, Integer, Long> {

        @Override
        protected Long doInBackground(Command... commands) {
            for (Command command : commands)
                try {
                    URL url = new URL(Constants.remoteBaseUrl + ":8803/command/" + ConfigurationService.getVIN() + "/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setDoOutput(true);
                    connection.setReadTimeout(1000);
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
                    e.printStackTrace();
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
