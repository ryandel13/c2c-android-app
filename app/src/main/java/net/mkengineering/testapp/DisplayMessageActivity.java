package net.mkengineering.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import net.mkengineering.testapp.objects.DataResponse;
import net.mkengineering.testapp.objects.ResponseEntity;
import net.mkengineering.testapp.tasks.JSONRequestTask;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class DisplayMessageActivity extends AppCompatActivity {

    private static DisplayMessageActivity uiObject;

    public static DisplayMessageActivity getUiObject() {
        return uiObject;
    }

    private Handler handler;

    public DisplayMessageActivity() {
        if(uiObject == null) {
            uiObject = this;
        }
    }

    public void setText(String text) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(textView.getText() + text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(Temperature.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);
        //fillChart();

        JSONRequestTask requestTask = new JSONRequestTask();
        try {
            requestTask.execute(new URL("http://localhost"));
        }
        catch(Exception e){}

        //this.updateGraph();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        SECONDS.sleep(1);
                        DisplayMessageActivity.getUiObject().updateGraph();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    private void fillChart() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1f),
                new DataPoint(1, 5f),
                new DataPoint(2, 3f)
        });
        graph.addSeries(series);
    }

    private void updateGraph() {
        JSONRequestTask requestTask = new JSONRequestTask();
        try {
            requestTask.execute(new URL("http://localhost"));
        }
        catch(Exception e){}
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private class Repeater extends Thread {

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            while(true) {
                System.out.println("blubb");
                try {
                    SECONDS.sleep(1);
                }
                catch (Exception e) {

                }
            }
        }
    }

    public void setJSON(Object jsonObject) {
        DataResponse data = (DataResponse) jsonObject;
        GraphView graph = (GraphView) findViewById(R.id.graph);

        DataPoint[] points = new DataPoint[data.values.size()];
        int i = 0;
        for(ResponseEntity rE : data.values) {
            DataPoint dp = new DataPoint(i, Float.parseFloat(rE.value));
            points[i++] = dp;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        graph.removeAllSeries();
        graph.addSeries(series);
    }
}
