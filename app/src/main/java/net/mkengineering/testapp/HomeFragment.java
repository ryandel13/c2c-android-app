package net.mkengineering.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.mkengineering.testapp.services.BluetoothManager;

/**
 * Created by MalteChristjan on 03.10.2017.
 */

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        BluetoothManager btManager = new BluetoothManager();
        btManager.isConnected();


        return inflater.inflate(R.layout.view_home, container, false);
    }

}
