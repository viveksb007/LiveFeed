package com.viveksb007.livefeed;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viveksb007 on 9/2/17.
 */

public class ClientFeed extends Activity {

    public static boolean LOOP_FOR_FRAME = true;
    @BindView(R.id.img_view)
    ImageView feedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_feed);
        ButterKnife.bind(this);
        ClientThread clientThread = new ClientThread(getIntent().getStringExtra("ip"), Integer.valueOf(getIntent().getStringExtra("port")));
        clientThread.start();
    }

    public class ClientThread extends Thread {

        private String ip;
        private int port;

        public ClientThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            Socket socket;
            DataInputStream inputStream = null;
            try {
                socket = new Socket(ip, port);
                inputStream = new DataInputStream(socket.getInputStream());
                while (LOOP_FOR_FRAME){
                    /* Receive Image Data and show it on ImageView.
                    inputStream.read();
                    */
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
