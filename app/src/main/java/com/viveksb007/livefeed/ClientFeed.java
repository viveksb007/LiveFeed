package com.viveksb007.livefeed;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viveksb007 on 9/2/17.
 */

public class ClientFeed extends Activity {

    public static boolean LOOP_FOR_FRAME = true;
    @BindView(R.id.web_view_feed)
    WebView clientView;
    @BindView(R.id.tv_client_feed)
    TextView tvClientFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.client_feed);
        ButterKnife.bind(this);
        String ip = getIntent().getStringExtra("ip");
        Integer port = Integer.valueOf(getIntent().getStringExtra("port"));
        tvClientFeed.setText("Feed from " + ip);
        clientView.setWebViewClient(new MyClient());
        clientView.loadUrl("http://" + ip + ":" + port);
    }

    private class MyClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    /*
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
                    //Receive Image Data and show it on ImageView.
                    inputStream.read();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    */

}
