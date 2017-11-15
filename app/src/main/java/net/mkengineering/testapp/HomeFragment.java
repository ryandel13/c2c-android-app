package net.mkengineering.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.mkengineering.testapp.objects.DataResponse;
import net.mkengineering.testapp.objects.ResponseEntity;
import net.mkengineering.testapp.services.CloudManager;
import net.mkengineering.testapp.tasks.HomeUpdateTask;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by MalteChristjan on 03.10.2017.
 */

public class HomeFragment extends Fragment {

    private static HomeFragment instance;

    private Boolean wasUpdated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        instance = this;

        Thread thread = new Thread() {
            @Override
            @SneakyThrows
            public void run() {
                while(wasUpdated) {
                    buildRun();
                    SECONDS.sleep(3);
                }
            }
        };

        thread.start();

        return inflater.inflate(R.layout.view_home, container, false);
    }

    @SneakyThrows
    private void buildRun() {
        HomeUpdateTask hut = new HomeUpdateTask();
        String sURL = "http://ryandel.selfhost.me:8802/vehicle/WP0ZZZ94427/";
        if(!(new CloudManager()).isConnected()) {
            sURL = "http://192.168.0.100:8802/vehicle/WP0ZZZ94427/";
        }
        // Connect to the URL using java's native library
        URL url = new URL(sURL);
        hut.execute(url);
    }

    public static HomeFragment getUiObject() {
        return instance;
    }

    public void updateHomeScreen(Object jsonObject) {
        wasUpdated = true;
        DataResponse data = (DataResponse) jsonObject;
        Map<String, ResponseEntity> map = new HashMap<>();
        for (ResponseEntity rE : data.getValues()) {
            map.put(rE.getName(), rE);
        }

        TextView home_license = (TextView) getView().findViewById(R.id.home_license);
        home_license.setText(map.get("licensePlate").getValue());

        TextView home_motor = (TextView) getView().findViewById(R.id.home_motorcode);
        home_motor.setText(map.get("motorCode").getValue());

        getView().findViewById(R.id.home_license).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.home_motorcode).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.home_separator).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }

}
