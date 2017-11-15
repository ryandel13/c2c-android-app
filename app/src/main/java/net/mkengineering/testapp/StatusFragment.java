package net.mkengineering.testapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import net.mkengineering.testapp.objects.DataResponse;
import net.mkengineering.testapp.objects.ResponseEntity;
import net.mkengineering.testapp.services.ConfigurationService;
import net.mkengineering.testapp.services.RemoteUrlBuilder;
import net.mkengineering.testapp.tasks.JSONRequestTask;

import java.net.URL;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by MalteChristjan on 03.10.2017.
 */

public class StatusFragment extends Fragment {

    private static StatusFragment uiObject;
    private static Handler mHandler;

    public StatusFragment() {


        if(uiObject == null) {
            uiObject = this;
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {

                }
            };
        }
    }

    public static StatusFragment getUiObject() {
        return uiObject;
    }

    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        SECONDS.sleep(1);
                        StatusFragment.getUiObject().updateGraph();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        return inflater.inflate(R.layout.view_status, container, false);
    }

    private void updateGraph() {
        JSONRequestTask requestTask = new JSONRequestTask();
        try {

            URL inside = RemoteUrlBuilder.getUriFor(
                    RemoteUrlBuilder.SERVICE.VDS, "vehicle", "temperature_outside/history", ConfigurationService.getRemoteUrl());//"http://ryandel.selfhost.me:8801/vehicle/WP0ZZZ94427/temperature_inside/history");
            URL outside = RemoteUrlBuilder.getUriFor(
                    RemoteUrlBuilder.SERVICE.VDS, "vehicle", "temperature_inside/history", ConfigurationService.getRemoteUrl());
            URL engine = RemoteUrlBuilder.getUriFor(
                    RemoteUrlBuilder.SERVICE.VDS, "vehicle", "temperature_engine/history", ConfigurationService.getRemoteUrl());

            requestTask.execute(inside, outside, engine);
        }
        catch(Exception e){}
    }

    public void setJSON(Object jsonObject, GraphType type) {
        DataResponse data = (DataResponse) jsonObject;
        try {
            GraphView graph;
            switch(type) {
                case INSIDE: graph = (GraphView) getView().findViewById(R.id.graph_inner);
                    break;
                case OUTSIDE: graph = (GraphView) getView().findViewById(R.id.graph_outer);
                    break;
                case ENGINE: graph = (GraphView) getView().findViewById(R.id.graph_engine);
                    break;
                default: graph = null;
            }

            if(graph == null) {
                return;
            }

            DataPoint[] points = new DataPoint[data.values.size()];
            int i = 0;
            for (ResponseEntity rE : data.values) {
                double value = Double.parseDouble(rE.value);
                value = round(value, 1);
                DataPoint dp = new DataPoint(i, value);
                points[i++] = dp;
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
            graph.removeAllSeries();
            graph.addSeries(series);
        }catch (Exception ex) {

        }
    }

    public enum GraphType {INSIDE, OUTSIDE, ENGINE}

}
