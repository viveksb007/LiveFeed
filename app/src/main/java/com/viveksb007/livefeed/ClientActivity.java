package com.viveksb007.livefeed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viveksb007 on 9/2/17.
 */

public class ClientActivity extends AppCompatActivity {

    @BindView(R.id.et_ip_address)
    EditText etIPAddress;
    @BindView(R.id.et_port_num)
    EditText etPortNum;
    @BindView(R.id.btn_connect)
    Button btnConnect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_activity);
        ButterKnife.bind(this);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = etIPAddress.getText().toString();
                String portNum = etPortNum.getText().toString();
                if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(portNum)) {
                    Toast.makeText(ClientActivity.this, "Enter proper data.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent clientLiveFeed = new Intent(ClientActivity.this, ClientFeed.class);
                clientLiveFeed.putExtra("ip", ip);
                clientLiveFeed.putExtra("port", portNum);
                startActivity(clientLiveFeed);
            }
        });
    }

}
