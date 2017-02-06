package com.viveksb007.livefeed;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    @BindView(R.id.btn_open_hotspot)
    Button btnOpenHotspot;
    @BindView(R.id.btn_live_feed)
    Button btnLiveFeed;

    public WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnOpenHotspot.setOnClickListener(this);
        btnLiveFeed.setOnClickListener(this);

        mWifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open_hotspot:
                openHotspot();
                break;
            case R.id.btn_live_feed:
                if (checkCameraHardware()) {
                    Intent liveFeed = new Intent(MainActivity.this, LiveFeed.class);
                    startActivity(liveFeed);
                } else {
                    Toast.makeText(this, "No Camera Detected", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean checkCameraHardware() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void openHotspot() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "LiveFeed";
        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        try {
            Method setWifiAPMethod = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apStatus = (Boolean) setWifiAPMethod.invoke(mWifiManager, wifiConfiguration, true);

            Method isWifiAPEnabled = mWifiManager.getClass().getMethod("isWifiApEnabled");

            Method getWifiAPStateMethod = mWifiManager.getClass().getMethod("getWifiApState");
            int apState = (Integer) getWifiAPStateMethod.invoke(mWifiManager);

            Method getWifiAPConfigMethod = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            wifiConfiguration = (WifiConfiguration) getWifiAPConfigMethod.invoke(mWifiManager);

            Log.v("Client", "SSID\n" + wifiConfiguration.SSID + "\nPassword : " + wifiConfiguration.preSharedKey);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

    private void closeHotspot() {
        Method setWifiAPMethod;
        try {
            setWifiAPMethod = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apStatus = (Boolean) setWifiAPMethod.invoke(mWifiManager, null, false);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeHotspot();
    }


}
