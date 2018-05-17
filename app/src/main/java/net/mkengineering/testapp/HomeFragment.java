package net.mkengineering.testapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.mkengineering.studies.sds.DataResponse;
import net.mkengineering.studies.sds.ResponseEntity;
import net.mkengineering.testapp.tasks.HomeUpdateTask;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * Created by MalteChristjan on 03.10.2017.
 */

public class HomeFragment extends Fragment {

    private static HomeFragment instance;

    @Getter
    private static Handler mHandler;

    public HomeFragment() {
        if (instance == null) {
            instance = this;
            mHandler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(Message inputMessage) {
                    Log.i("HomeFragmentHandler", "Handling incoming message");
                    updateHomeScreen(((HomeUpdateTask.HomeFragmentMessage) inputMessage.obj).getDataResponse());
                }
            };
        }
    }

    public static HomeFragment getUiObject() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        instance = this;

        return inflater.inflate(R.layout.view_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Thread thread = new Thread(new HomeUpdateTask());
        thread.start();
    }


    private Boolean updateHomeScreen(DataResponse jsonObject) {
        DataResponse data = jsonObject;
        Map<String, ResponseEntity> map = new HashMap<>();
        for (ResponseEntity rE : data.getValues()) {
            map.put(rE.getName(), rE);
        }

        try {
            TextView home_license = (TextView) getView().findViewById(R.id.home_license);
            home_license.setText(map.get("licensePlate").getValue());

            TextView home_motor = (TextView) getView().findViewById(R.id.home_motorcode);
            home_motor.setText(map.get("motorCode").getValue());

            getView().findViewById(R.id.home_license).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.home_motorcode).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.home_separator).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
