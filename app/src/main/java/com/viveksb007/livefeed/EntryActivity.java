package com.viveksb007.livefeed;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by viveksb007 on 9/2/17.
 */

public class EntryActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_client)
    Button btnClient;
    @BindView(R.id.btn_server)
    Button btnServer;

    String[] perms = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);
        ButterKnife.bind(this);
        btnClient.setOnClickListener(this);
        btnServer.setOnClickListener(this);
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale), 1, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_client:
                Intent client = new Intent(EntryActivity.this, ClientActivity.class);
                startActivity(client);
                break;
            case R.id.btn_server:
                Intent server = new Intent(EntryActivity.this, MainActivity.class);
                startActivity(server);
                break;
        }
    }
}
