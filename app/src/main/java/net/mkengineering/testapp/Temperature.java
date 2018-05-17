package net.mkengineering.testapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.mkengineering.testapp.services.CommandService;

public class Temperature extends FragmentActivity {

    private TextView mTextMessage;

    private static Temperature instance;

    public static final String EXTRA_MESSAGE = "net.mkengineering.testapp.MESSAGE";

    private CommandService cmdService = new CommandService();

    private Fragment cmdFragment = new CommandView();

    private Fragment homeFragment = new HomeFragment();

    private Fragment statudFragment = new StatusFragment();

    public Temperature() {
        if(instance == null) {
            instance = this;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    openFragment(homeFragment);
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText("Commands");
                    openFragment(cmdFragment);
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText("Status");
                    openFragment(statudFragment);
                    return true;
            }
            return false;
        }

    };

    public void sendMessage(View view) {
        /*Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);*/


    }

    public static String getCurrentSsid() {
        String ssid = null;
        Context context = instance.getApplication().getApplicationContext();
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    public void executeLock(View view) {

        cmdService.sendCommand(CommandService.COMMAND.LOCK);
    }

    private void openFragment(Fragment fragment) {
        //Bundle args = new Bundle();
        //args.putInt(CommandView.ARG_POSITION, position);
        //fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.content, fragment);
        //transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.content) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            //Fragment firstFragment = new HomeFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            homeFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.content, homeFragment).commit();
        }

        /*
        mTextMessage = (TextView) findViewById(R.id.message);*/
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



    public static void makeToast(String toast) {
        Toast.makeText(instance.getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }

    public static void registerReceiverRemote(BroadcastReceiver receiver, IntentFilter filter) {
        instance.registerReceiver(receiver, filter);
    }

    public static FragmentActivity getInstance() {
        return instance;
    }
}
